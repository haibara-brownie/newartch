package com.cloudwise.archetype.commons.response;

public interface BaseExceptionCode {

    int SUCCESS = 100000;
    String SYSTEM_ERROR = "100001";
    String ARGS_TYPE_MISMATCH_ERROR = "100002";
    String ERROR_VALIDATE_PARAMETER = "100003";
    String MISSING_SERVLET_REQUEST_PARAMETER = "100004";
    String HTTP_REQUEST_METHOD_NOT_SUPPORTED = "100005";


}