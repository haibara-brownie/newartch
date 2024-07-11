package com.cloudwise.archetype.commons.response.advice;

import com.cloudwise.archetype.commons.response.BaseException;
import com.cloudwise.archetype.commons.response.BaseExceptionCode;
import com.cloudwise.archetype.commons.response.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * @author jiayongming
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.cloudwise.archetype")
public class ApiExceptionHandle implements BaseExceptionCode {

    /**
     * 处理Get请求中 使用@Valid 验证路径中请求实体校验失败后抛出的异常
     *
     * @param e BindException
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    public <K> ApiUnifiedResult<K> bindException(BindException e) {
        return ApiUnifiedResult.getErrorResult(e.getFieldErrors()
                .stream()
                .map(item -> String.valueOf(item.getDefaultMessage()))
                .findFirst()
                .orElse(SYSTEM_ERROR));
    }

    /**
     * 处理请求参数格式错误 @RequestBody上validate失败后抛出的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public <K> ApiUnifiedResult<K> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorCode = bindingResult.getFieldErrors().stream().map(item -> String.valueOf(item.getDefaultMessage())).findFirst().orElse(ERROR_VALIDATE_PARAMETER);
        if (NumberUtils.toInt(errorCode, -1) != -1) {
            return ApiUnifiedResult.getErrorResult(errorCode);
        } else {
            return ApiUnifiedResult.getErrorResult(Integer.parseInt(ERROR_VALIDATE_PARAMETER), errorCode);
        }
    }

    /**
     * validation验证异常
     * 处理请求参数格式错误 @RequestParam上validate失败后抛出的异常
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public <K> ApiUnifiedResult<K> constraintViolationException(ConstraintViolationException e) {
        final Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        final String errorInfo = constraintViolations.stream().map(ConstraintViolation::getMessage).filter(StringUtils::isNotBlank).findFirst().orElse(SYSTEM_ERROR);
        return ApiUnifiedResult.getErrorResult(errorInfo);
    }

    /**
     * 参数类型异常
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public <K> ApiUnifiedResult<K> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ApiUnifiedResult.getErrorResult(new BaseException(ARGS_TYPE_MISMATCH_ERROR, ex.getName()));
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public <K> ApiUnifiedResult<K> missingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ApiUnifiedResult.getErrorResult(new BaseException(MISSING_SERVLET_REQUEST_PARAMETER, e.getParameterName()));
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public <K> ApiUnifiedResult<K> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ApiUnifiedResult.getErrorResult(new BaseException(HTTP_REQUEST_METHOD_NOT_SUPPORTED, e.getMethod()));
    }

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class})
    public <K> ApiUnifiedResult<K> argumentException(Exception e) {
        if (!StringUtils.isNumeric(e.getMessage())) {
            log.error("ApiExceptionHandle argumentException {}", LogUtils.getLogException(e));
        }
        return ApiUnifiedResult.getErrorResult(e);
    }

    @ExceptionHandler({BaseException.class})
    public <K> ApiUnifiedResult<K> baseException(BaseException e) {
        if (!StringUtils.isNumeric(e.getCode())) {
            log.error("ApiExceptionHandle BaseException {}", LogUtils.getLogException(e));
        }
        return ApiUnifiedResult.getErrorResult(e);
    }

    @ExceptionHandler(value = Exception.class)
    public <K> ApiUnifiedResult<K> exception(Exception e) {
        log.error("ApiExceptionHandle Exception {}", LogUtils.getLogException(e));
        return ApiUnifiedResult.getErrorResult(e);
    }
}