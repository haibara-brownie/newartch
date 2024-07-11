package com.cloudwise.archetype.commons.response.advice;

import lombok.Getter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

/**
 * Created by jiayongming on 2022/10/27.
 * 接口直接返回(data为返回内容)
 */
public class ApiDirectResult {
    @Getter
    private final Object data;

    private ApiDirectResult(Object data) {
        this.data = Objects.requireNonNull(data, "null source");
    }

    public static ApiDirectResult getInstance(Object data) {
        return new ApiDirectResult(data);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

}
