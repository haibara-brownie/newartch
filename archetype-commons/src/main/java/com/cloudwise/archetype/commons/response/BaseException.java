package com.cloudwise.archetype.commons.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private Object[] args;
    private String code = BaseExceptionCode.SYSTEM_ERROR;
    private Object errors;

    public BaseException(String code) {
        this.code = code;
    }

    public BaseException(String code, Object[] args) {
        this.code = code;
        this.args = args;
    }

    public BaseException(String code, String... args) {
        this.code = code;
        this.args = args;
    }

    public BaseException(Throwable cause, String code) {
        super(cause);
        this.code = code;
    }

}