package com.cloudwise.archetype.main;

import com.cloudwise.archetype.commons.decrypt.DecryptUtils;
import com.cloudwise.cache.lettuce.annotations.EnableLettuceRedis;
import com.cloudwise.cache.lettuce.annotations.EnableSubcribe;
import com.cloudwise.msg.annotations.Msg;
import com.cloudwise.storage.EnableFileStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.CountDownLatch;

/**
 * @author jiayongming
 */
@Slf4j
@EnableAsync
@EnableScheduling
@Msg
@EnableFileStorage
@EnableLettuceRedis
@EnableSubcribe
@EnableDiscoveryClient
@EnableDubbo(scanBasePackages = "com.cloudwise.archetype")
@SpringBootApplication(scanBasePackages = {
        "com.cloudwise.archetype"
}, exclude = {SecurityAutoConfiguration.class})
public class MainApplication {

    @Bean
    public CountDownLatch closeLatch() {
        return new CountDownLatch(1);
    }

    public static void main(String[] args) {
        try {
            // 解密nacos密码
            DecryptUtils.decryptNacosPassword();

            ApplicationContext ctx = new SpringApplicationBuilder()
                    .sources(MainApplication.class)
                    .registerShutdownHook(false)
                    .run(args);

            final String applicationName = ctx.getEnvironment().getProperty("spring.application.name");
            final String profiles = ctx.getEnvironment().getProperty("spring.profiles.active");
            final String port = ctx.getEnvironment().getProperty("server.port");

            CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
            log.info("===> {} Start in profiles:{} successful port:{}", applicationName, profiles, port);
            closeLatch.await();

        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("{} 程序中断异常退出", MainApplication.class.getName(), e);
        }
    }

}