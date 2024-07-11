package com.cloudwise.archetype.commons.filter;

import lombok.Data;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiayongming
 * filter管理
 */
@Configuration
@Data
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean<LocaleHttpFilter> logInfoDTOFilterRegister() {
        FilterRegistrationBean<LocaleHttpFilter> registration = new FilterRegistrationBean<>();
        //注入过滤器
        registration.setFilter(new LocaleHttpFilter());
        //拦截规则
        registration.addUrlPatterns("/*");
        //过滤器名称
        registration.setName("localeHttpFilter");
        //过滤器顺序
        registration.setOrder(5);
        return registration;
    }


}
