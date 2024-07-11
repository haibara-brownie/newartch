package com.cloudwise.archetype.commons.response.log;

import com.cloudwise.archetype.commons.response.BaseException;
import com.cloudwise.archetype.commons.response.message.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Objects;

/**
 * @author jiayongming
 */
@Slf4j
public class LogUtils {

    public static String getLogException(Throwable e) {
        if (Objects.isNull(e)) {
            return null;
        }
        StringBuilder error = new StringBuilder();
        if (e instanceof BaseException) {
            BaseException exception = (BaseException) e;
            error.append("code:").append(exception.getCode());
            String message = MessageUtils.getMsg(exception);
            if (StringUtils.isNotBlank(message)) {
                error.append(", message:").append(message);
            }
        }
        String errorMessage = e.getMessage();
        if (StringUtils.isNotBlank(errorMessage)) {
            error.append(System.lineSeparator()).append("error message:").append(errorMessage);
        }
        if (StringUtils.isNotBlank(ExceptionUtils.getStackTrace(e))) {
            error.append(System.lineSeparator()).append(ExceptionUtils.getStackTrace(e));
        }
        return error.toString();
    }

}