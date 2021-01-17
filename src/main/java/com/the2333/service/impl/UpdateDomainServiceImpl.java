package com.the2333.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.the2333.config.ConfigAliyun;
import com.the2333.service.DomainHandle;
import com.the2333.service.UpdateDomainService;
import com.the2333.util.IpUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 描述:
 * 更新域名记录
 *
 * @author lil‘s
 * @create 2021-01-16 8:53
 */
@Slf4j
@Component
public class UpdateDomainServiceImpl implements UpdateDomainService {

    private int count = 1;

    /**
     * 域名处理类, 从配置文件中读取信息并加载
     */
    private final DomainHandle domainHandle;

    /**
     * 配置信息
     */
    private final ConfigAliyun configAliyun;

    public UpdateDomainServiceImpl(DomainHandle domainHandle, ConfigAliyun configAliyun) {
        this.domainHandle = domainHandle;
        this.configAliyun = configAliyun;
    }

    /**
     * 更新域名, 依赖于配置文件信息
     *
     * 代码起点在这
     * @see com.the2333.config.DynamicCronSchedule#configureTasks(org.springframework.scheduling.config.ScheduledTaskRegistrar)
     */
    @Override
    public void updateDomain() {
        Map<String, List<String>> domainMap = domainHandle.getDomainMap();
        if (CollectionUtils.isEmpty(domainMap)) {
            log.error("域名更新失败, 请检查配置文件信息");
            return;
        }
        String currentHostIp = IpUtil.getCurrentHostIp();
        // 遍历更新域名记录
        domainMap.forEach((domain, rr) -> {
            try {
                this.updateDomainRecord(domain, rr, currentHostIp);
            } catch (Exception e) {
                log.error("阿里云SDK请求失败, 域名更新失败. 域名: {}, 主机记录: {}, 当前ip地址: {}", domain, rr, currentHostIp, e);
            }
        });
    }

    /**
     * 更新阿里云记录
     *
     * @param domainName 域名 例: qq.com
     * @param rrList     一个域名下需要更新的一个或多个主机记录
     * @param ip         当前机器IP地址
     */
    @SneakyThrows
    private void updateDomainRecord(String domainName, List<String> rrList, String ip) {
        log.debug("方法执行第{}次", count++);
        IAcsClient client = this.setProfile();
        DescribeDomainRecordsRequest request = new DescribeDomainRecordsRequest();
        // 设置主域名
        request.setDomainName(domainName);
        // 调用SDK发送请求
        DescribeDomainRecordsResponse response = client.getAcsResponse(request);
        List<DescribeDomainRecordsResponse.Record> records = response.getDomainRecords();
        records.forEach(record -> {
            if (log.isDebugEnabled()) {
                log.debug("准备检查 {}.{} 解析记录是否匹配, 当前域名IP地址: {}, 当前主机IP地址: {}",
                        record.getRR(), record.getDomainName(), record.getValue(), ip);
            }
            rrList.forEach(rr -> {
                // 如果解析记录不为空 且 解析记录一致 且 解析记录与当前主机IP不一致 执行更新操作
                if (record.getRR() != null && record.getRR().equals(rr) && !record.getValue().equals(ip)) {
                    try {
                        this.updateOne(record, ip, client);
                    } catch (Exception e) {
                        log.error("阿里云SDK请求失败, 域名更新失败. 域名: {}, 主机记录: {}, 当前ip地址: {}", domainName, rr, ip, e);
                    }
                }
            });
        });
    }

    /**
     * 设置鉴权参数
     *
     * @return Profile
     */
    private IAcsClient setProfile() {
        // 无效的key
        if (Objects.isNull(configAliyun) || StringUtils.isBlank(configAliyun.getRegionId())
                || StringUtils.isBlank(configAliyun.getAccessKeyId()) || StringUtils.isBlank(configAliyun.getSecret())) {
            log.error("配置信息错误. {}", configAliyun);
            throw new RuntimeException("配置信息不能为空");
        }
        DefaultProfile profile = DefaultProfile.getProfile(configAliyun.getRegionId()
                , configAliyun.getAccessKeyId(), configAliyun.getSecret());
        return new DefaultAcsClient(profile);
    }

    /**
     * 更新域名记录
     *
     * @param record 阿里云解析记录
     * @param ip     当前主机ip
     * @param client 配置信息
     * @throws Exception exc
     */
    private void updateOne(DescribeDomainRecordsResponse.Record record, String ip, IAcsClient client) throws Exception {
        // 修改解析记录
        UpdateDomainRecordRequest request = new UpdateDomainRecordRequest();
        // 记录ID
        request.setRecordId(record.getRecordId());
        // 主机记录 例: music.qq.com 中的music
        request.setRR(record.getRR());
        // 解析记录类型 例: A类型(指向一个IVP4地址)
        request.setType(record.getType());
        // 设置成当前主机IP地址
        request.setValue(ip);
        // 域名TTL(生存时间值) 这里没有进行自定义, 具体值查看阿里云域名的TTL
        request.setTTL(record.getTTL());
        // 调用SDK发送请求
        UpdateDomainRecordResponse response = client.getAcsResponse(request);
        log.info("域名 {}.{} 修改成功, 当前主机ip地址: {}", record.getRR(), record.getValue(), ip);
    }
}
