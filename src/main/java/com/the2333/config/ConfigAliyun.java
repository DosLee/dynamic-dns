package com.the2333.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 描述:
 * 必要参数
 *
 * @author lil‘s
 * @create 2021-01-16 9:13
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun")
public class ConfigAliyun {
    /**
     * 地域ID
     */
    private String regionId;
    /**
     * 阿里云账号 AccessKey ID
     */
    private String accessKeyId;
    /**
     * 阿里云账号 AccessKey Secret
     */
    private String secret;

    /**
     * 域名更新时间
     */
    private String cron;
}
