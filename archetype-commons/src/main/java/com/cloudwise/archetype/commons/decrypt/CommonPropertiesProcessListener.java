package com.cloudwise.archetype.commons.decrypt;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description:
 * date: 2022/1/15 9:23 上午
 *
 * @author jamin.jia
 */
@Slf4j
public class CommonPropertiesProcessListener implements ApplicationListener<ApplicationPreparedEvent> {

    private static final String DECRYPT_PROP = "props.decrypt.props";
    private static final String DECRYPT_NOPROP = "props.decrypt.noprops";
    private static final String SEPARATOR = ",";
    private static final String DECRYPT_PROPERTY_SOURCE = "cloudwise-common-decrypt";

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent applicationPreparedEvent) {

        ConfigurableEnvironment environment = applicationPreparedEvent.getApplicationContext().getEnvironment();

        MutablePropertySources propertySources = environment.getPropertySources();
        //nacos账号密码解密
        List<String> processDecryptKeys = Arrays.stream(environment.getProperty(DECRYPT_PROP, StringUtils.EMPTY).split(SEPARATOR)).collect(Collectors.toList());
        List<String> noprocessDecryptKeys = Arrays.stream(environment.getProperty(DECRYPT_NOPROP, StringUtils.EMPTY).split(SEPARATOR)).collect(Collectors.toList());
        log.info("preparedDecryptKeyList : {}", processDecryptKeys);
        log.info("noprocessDecryptKeyList : {}", noprocessDecryptKeys);
        propertySources.remove(DECRYPT_PROPERTY_SOURCE);
        Set<String> keys = processDecryptKeys.stream()
                .filter(StringUtils::isNotBlank)
                .filter(item -> !noprocessDecryptKeys.contains(item))
                .map(String::trim).collect(Collectors.toSet());
        processDecryptProperties(propertySources, environment, keys);
    }

    private void processDecryptProperties(MutablePropertySources propertySources,
                                          ConfigurableEnvironment environment,
                                          @NonNull Set<String> keys) {
        Properties properties = new Properties();
        keys.forEach(key -> {
            String value = environment.getProperty(key, StringUtils.EMPTY);
            if (StringUtils.isNoneBlank(value)) {
                String decrypt = DecryptUtils.decrypt(value);
                properties.setProperty(key, decrypt);
            }
        });
        PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource(DECRYPT_PROPERTY_SOURCE, properties);
        propertySources.addFirst(propertiesPropertySource);
    }

}