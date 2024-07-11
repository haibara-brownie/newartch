package com.cloudwise.archetype.dao.configuration;

import cn.hutool.extra.spring.SpringUtil;
import com.cloudwise.archetype.commons.decrypt.DecryptUtils;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Data
@RefreshScope
@Slf4j
@Configuration
public class DataSourceConfiguration {
    
    @Value("${spring.datasource.url:}")
    private String dbUrl;

    @Value("${spring.datasource.username:}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @Value("${spring.datasource.driver-class-name:#{null}}")
    private String driverClassName;
    
    @Primary
    @Bean
    @RefreshScope
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariDataSource hikariDataSource() {
        if (StringUtils.isAnyBlank(dbUrl, username, password)) {
            dbUrl = SpringUtil.getProperty("panguDBWriteJdbcUrl");
            username = SpringUtil.getProperty("panguDBWriteUser");
            password = SpringUtil.getProperty("panguDBWritePwd");
        }
        if (StringUtils.isAnyBlank(dbUrl, username, password)) {
            dbUrl = SpringUtil.getProperty("postgresqlWriteJdbcUrl");
            username = SpringUtil.getProperty("postgresqlWriteUser");
            password = SpringUtil.getProperty("postgresqlWritePwd");
        }
        if (StringUtils.isAnyBlank(dbUrl, username, password)) {
            log.error("关系型数据库配置异常，请检查配置");
            return new HikariDataSource();
        }
        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(this.getPassword());
        datasource.setDriverClassName(DatabaseDriverName.getDriverClassNameByUrl(dbUrl).orElse(driverClassName));
        return datasource;
    }

    public String getPassword() {
        return DecryptUtils.decrypt(password);
    }

}
