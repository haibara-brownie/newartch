package com.cloudwise.archetype.commons.util;

import cn.hutool.core.util.StrUtil;
import com.cloudwise.archetype.commons.response.BaseExceptionCode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class MessUtilsTest {

    private static final Map<String, Properties> MESSAGE_MAP = new HashMap<>();
    private static final List<Locale> LOCALES = ImmutableList.of(Locale.CHINA, Locale.US);
    private static final String BASE_NAME = "messages";

    static {
        for (Locale locale : LOCALES) {
            try {
                EncodedResource encodedResource = new EncodedResource(new ClassPathResource(BASE_NAME + StrUtil.UNDERLINE + locale.toString() + ".properties"), StandardCharsets.UTF_8);
                MESSAGE_MAP.put(locale.getLanguage(), PropertiesLoaderUtils.loadProperties(encodedResource));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

    }

    @Test
    public void test1() throws NoSuchFieldException, IllegalAccessException {
        Properties zh = MESSAGE_MAP.get(Locale.CHINA.getLanguage());
        Properties en = MESSAGE_MAP.get(Locale.US.getLanguage());
        List<String> strs = Lists.newArrayListWithCapacity(640);
        Field[] fields = BaseExceptionCode.class.getFields();
        for (Field f : fields) {
            Object o = BaseExceptionCode.class.getField(f.getName()).get(null);
            strs.add(String.valueOf(o));
        }
        String format = String.format("zh错误数量:%s; en错误数量:%s; ExceptionCode数量%s。", zh.size(), en.size(), strs.size());
        System.out.println(format);

        zh.forEach((k, v) -> {
            Object o = en.get(k);
            if (Objects.isNull(o) && NumberUtils.toLong(String.valueOf(k)) < 300000) {
                System.out.printf("1.未翻译的语句(archetype)key:%s=%s%n", k, zh.get(k));
            }
        });

        en.forEach((k, v) -> {
            Object o = zh.get(k);
            if (Objects.isNull(o)) {
                System.out.printf("2.en中包含且zh未含的key:%s%n", k);
            }
        });

        zh.forEach((k, v) -> {
            boolean contains = strs.contains(String.valueOf(k));
            if (!contains) {
                System.out.printf("3.zh中包含且ExceptionCode未含的key:%s%n", k);
            }
        });

        en.forEach((k, v) -> {
            boolean contains = strs.contains(String.valueOf(k));
            if (!contains) {
                System.out.printf("4.en中包含且ExceptionCode未含的key:%s%n", k);
            }
        });

        strs.forEach(k -> {
            Object o = zh.get(k);
            if (Objects.isNull(o)) {
                System.out.printf("5.ExceptionCode中包含且zh未含的key:%s%n", k);
            }
        });
    }

}
