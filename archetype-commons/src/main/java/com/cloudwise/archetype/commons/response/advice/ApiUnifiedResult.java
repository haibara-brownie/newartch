package com.cloudwise.archetype.commons.response.advice;

import com.cloudwise.archetype.commons.response.BaseException;
import com.cloudwise.archetype.commons.response.BaseExceptionCode;
import com.cloudwise.archetype.commons.response.message.MessageUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author jiayongming
 * @date 2023/1/18
 * 接口统一返回值
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
final class ApiUnifiedResult<T> implements Serializable {

    private static final long serialVersionUID = -6359770619752368082L;
    private static final String SUCCESS_STATUS = "success";
    private static final String FAIL_STATUS = "fail";

    @ApiModelProperty(value = "code标识")
    private int code;
    @ApiModelProperty(value = "错误信息")
    private String msg;
    @ApiModelProperty(value = "结果集")
    private T data;

    /**
     * 通用返回值
     */
    private static <T> ApiUnifiedResult<T> of(int code, String msg, T data) {
        msg = StringUtils.defaultIfBlank(msg, MessageUtils.getMsg(String.valueOf(code)));
        return ApiUnifiedResult.<T>builder().code(code).msg(msg).data(data).build();
    }

    /**
     * 成功返回值
     */
    static <T> ApiUnifiedResult<T> getOKResult(T data) {
        return of(BaseExceptionCode.SUCCESS, null, data);
    }

    /**
     * 失败返回值
     */
    static <K> ApiUnifiedResult<K> getErrorResult(String code) {
        if (StringUtils.isNumeric(code)) {
            return getErrorResult(Integer.parseInt(code), null);
        }
        return getErrorResult(Integer.parseInt(BaseExceptionCode.SYSTEM_ERROR), code);
    }

    private static <K> ApiUnifiedResult<K> getErrorResult(BaseException e) {
        String errorCode = e.getCode();
        if (StringUtils.isNumeric(errorCode)) {
            // code必须为数字，否则做普通异常错误信息处理
            return getErrorResult(Integer.parseInt(errorCode), MessageUtils.getMsg(e));
        } else {
            String errorInfo;
            if (StringUtils.isNotBlank(errorCode)) {
                errorInfo = errorCode;
            } else if (StringUtils.isNotBlank(e.getMessage())) {
                errorInfo = e.getMessage();
            } else {
                errorInfo = String.valueOf(e.getCause());
            }
            return getErrorResult(errorInfo);
        }
    }

    static <K> ApiUnifiedResult<K> getErrorResult(Exception e) {
        if (e instanceof BaseException) {
            return getErrorResult((BaseException) e);
        }
        return getErrorResult(e.getMessage());
    }

    static <K> ApiUnifiedResult<K> getErrorResult(int code, String msg) {
        return of(code, msg, null);
    }

    /**
     * 是否是成功的返回值
     */
    public boolean successfulResponse() {
        return this.getCode() == BaseExceptionCode.SUCCESS;
    }

    /**
     * 状态消息
     */
    public String getStatus() {
        return successfulResponse() ? SUCCESS_STATUS : FAIL_STATUS;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

}