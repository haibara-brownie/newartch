package com.cloudwise.archetype.commons.response.message;

import cn.hutool.core.util.StrUtil;
import com.cloudwise.archetype.commons.concurrent.ThreadLocalUtil;
import com.cloudwise.archetype.commons.response.BaseException;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public class MessageUtils {

    private static final List<Locale> LOCALES = ImmutableList.of(Locale.CHINA, Locale.US);
    private static final Multimap<String, MutablePair<String, String>> MULTI_MESSAGE = ArrayListMultimap.create(1024, 2);
    private static final String BASE_NAME = "messages";

    static {
        for (Locale locale : LOCALES) {
            try {
                EncodedResource encodedResource = new EncodedResource(new ClassPathResource(BASE_NAME + StrUtil.UNDERLINE + locale.toString() + ".properties"), StandardCharsets.UTF_8);
                Properties properties = PropertiesLoaderUtils.loadProperties(encodedResource);
                for (String key : properties.stringPropertyNames()) {
                    MULTI_MESSAGE.put(key, MutablePair.of(locale.getLanguage(), properties.getProperty(key)));
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

    }

    public static String getMsg(String code) {
        final String language = ThreadLocalUtil.getLanguage();
        Collection<MutablePair<String, String>> mutablePairs = MULTI_MESSAGE.get(code);
        Optional<MutablePair<String, String>> any = mutablePairs.stream().filter(item -> item.getLeft().equalsIgnoreCase(language)).findFirst();
        if (any.isPresent()) {
            return any.get().getRight();
        } else {
            Optional<MutablePair<String, String>> first = mutablePairs.stream().findFirst();
            return first.orElse(MutablePair.of(Locale.CHINA.getLanguage(), "系统错误")).getRight();
        }
    }

    public static String getMsg(String code, Object... args) {
        return String.format(getMsg(code), args);
    }

    public static String getMsg(BaseException e) {
        return getMsg(e.getCode(), e.getArgs());
    }
}