package com.cloudwise.archetype.middleware.xxljob.config;

import cn.hutool.extra.spring.SpringUtil;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxlJob 配置类
 *
 * @Author pio.wang
 * @Since: 2021-07-06 15:34
 */
@Slf4j
@Configuration
@Data
public class XxlJobConfig {
    
    @Value("${cwXxlJobUrl:}")
    private String baseUrl;

    @Value("${cwXxlJobUsername:}")
    private String userName;

    @Value("${cwXxlJobPassword:}")
    private String password;

    @Value("${xxl.job.accessToken:xxljob}")
    private String accessToken;

    @Value("${xxl.job.executor.address:}")
    private String address;

    @Value("${cwServiceHostname:127.0.0.1}")
    private String ip;

    @Value("${xxl.job.executor.port:-1}")
    private int port;

    @Value("${cwLogRootPath:./logs}")
    private String logPath;

    @Value("${xxl.job.executor.logretentiondays:7}")
    private int logRetentionDays;
    
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(this.getBaseUrl());
        xxlJobSpringExecutor.setAppname(SpringUtil.getApplicationName());
        if (StringUtils.isNotBlank(this.getAddress())) {
            xxlJobSpringExecutor.setAddress(this.getAddress());
        }
        xxlJobSpringExecutor.setIp(this.getIp());
        xxlJobSpringExecutor.setPort(this.getPort());
        xxlJobSpringExecutor.setAccessToken(this.getAccessToken());
        xxlJobSpringExecutor.setLogPath(this.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(this.getLogRetentionDays());
        return xxlJobSpringExecutor;
    }

}