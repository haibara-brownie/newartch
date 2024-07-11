package com.cloudwise.archetype.api.conf;

import com.cloudwise.douc.facade.AccountDubboFacade;
import com.cloudwise.douc.facade.DataDubboFacade;
import com.cloudwise.douc.facade.LogDubboFacade;
import com.cloudwise.douc.facade.UserGroupV2DubboFacade;
import com.cloudwise.douc.facade.UserV2DubboFacade;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@Slf4j
public class DubboReferenceConfig {

    @DubboReference(consumer = "consumerDouc")
    private AccountDubboFacade accountDubboFacade;

    @DubboReference(consumer = "consumerDouc")
    private UserGroupV2DubboFacade userGroupV2DubboFacade;

    @DubboReference(consumer = "consumerDouc")
    private UserV2DubboFacade userV2DubboFacade;

    @DubboReference(consumer = "consumerDouc")
    private DataDubboFacade dataDubboFacade;

    @DubboReference(consumer = "consumerDouc")
    private LogDubboFacade logDubboFacade;

    @Bean
    public AccountDubboFacade accountDubboFacade() {
        return accountDubboFacade;
    }

    @Bean
    public UserV2DubboFacade userV2DubboFacade() {
        return userV2DubboFacade;
    }

    @Bean
    public UserGroupV2DubboFacade userGroupV2DubboFacade() {
        return userGroupV2DubboFacade;
    }

    @Bean
    public DataDubboFacade dataDubboFacade() {
        return dataDubboFacade;
    }

    @Bean
    public LogDubboFacade logDubboFacade() {
        return logDubboFacade;
    }

}
