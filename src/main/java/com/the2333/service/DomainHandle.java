package com.the2333.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 描述:
 * 域名处理
 *
 * @author lil‘s
 * @create 2021-01-16 13:57
 */
@Slf4j
@Component
public class DomainHandle {

    @Value("${aliyun.domainNames}")
    private String domainNames;

    /**
     * 按逗号分割多个域名
     *
     * @return List<"music.qq.com">
     */
    private List<String> mergeDomain() {
        if (StringUtils.isBlank(domainNames)) {
            log.warn("配置文件中没有配置域名");
            return new ArrayList<>();
        }
        return Arrays.asList(domainNames.split(","));
    }

    /**
     * key: 域名, value: 一个或多个主机记录
     *
     * @return Map<"qq.com", List<"music", "sports">>
     */
    public Map<String, List<String>> getDomainMap() {
        List<String> domainList = mergeDomain();
        Map<String, List<String>> domainMap = new HashMap<>();
        if (CollectionUtils.isEmpty(domainList)) {
            log.warn("域名列表不能为空");
            return domainMap;
        }
        // 遍历域名
        for (String domains : domainList) {
            if (StringUtils.isBlank(domains)) {
                continue;
            }
            // 主机记录
            String rr = domains.substring(0, domains.indexOf("."));
            if (StringUtils.isBlank(rr)) {
                continue;
            }
            // 域名
            String domain = domains.substring(domains.indexOf(".") + 1).trim();
            if (StringUtils.isBlank(domain)) {
                continue;
            }
            // 添加域名到Map
            if (!domainMap.containsKey(domain)) {
                domainMap.put(domain, new ArrayList<>());
            }
            // 一个域名多个主机记录的情况
            domainMap.get(domain).add(rr.trim());
        }
        return domainMap;
    }
}
