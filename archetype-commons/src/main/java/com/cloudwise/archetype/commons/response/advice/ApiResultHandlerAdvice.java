package com.cloudwise.archetype.commons.response.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;

/**
 * Description: controller 切面结果正常处理
 * date: 2020/7/8 6:45 下午
 *
 * @author jamin.jia
 * 实现统一的ApiUnifiedResult返回:
 * 通用返回值：直接返回ApiUnifiedResult对象;
 * 1、成功返回值(code值固定)
 * ①成功返回值(无message，含data): 返回data内容(Object类型)即可；
 * ②成功返回值(无message，无data): 返回null或者void；
 * 2、失败返回值
 * ①直接返回异常即可(支持code码或错误信息)
 * <p>
 * 特殊返回值:
 * ①返回ApiDirectResult对象即可
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.cloudwise.archetype")
public class ApiResultHandlerAdvice implements ResponseBodyAdvice<Object> {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiDirectResult) {
            return ((ApiDirectResult) body).getData();
        }

        if (body instanceof ApiUnifiedResult || MediaType.APPLICATION_OCTET_STREAM.equals(selectedContentType)) {
            // 如果是 ApiUnifiedResult 及其子类或者文件流,不处理直接返回
            return body;
        } else if (selectedConverterType.isAssignableFrom(StringHttpMessageConverter.class)) {
            // Converter是String时返回字符串
            return objectMapper.writeValueAsString(ApiUnifiedResult.getOKResult(body));
        } else {
            return ApiUnifiedResult.getOKResult(body);
        }

    }

}