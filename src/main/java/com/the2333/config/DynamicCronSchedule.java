package com.the2333.config;

import com.the2333.service.UpdateDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 自定义cron表达式
 *
 * @author lil‘s
 * @create 2021-01-16 9:45
 */
@Slf4j
@Component
public class DynamicCronSchedule implements SchedulingConfigurer {

    private final ConfigAliyun configAliyun;
    private final UpdateDomainService updateDomainService;

    public DynamicCronSchedule(ConfigAliyun configAliyun, UpdateDomainService updateDomainService) {
        this.configAliyun = configAliyun;
        this.updateDomainService = updateDomainService;
    }

    /**
     * 动态传入Cron表达式, 执行域名更新方法
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(updateDomainService::updateDomain
                , triggerContext -> new CronTrigger(configAliyun.getCron()).nextExecutionTime(triggerContext));
    }
}
