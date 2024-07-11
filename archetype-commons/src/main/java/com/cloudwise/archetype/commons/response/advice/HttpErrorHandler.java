package com.cloudwise.archetype.commons.response.advice;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Created by jiayongming on 2022/10/26.
 */
@RestController
public class HttpErrorHandler implements ErrorController {
    
    /**
     * Supports other formats like JSON, XML
     */
    @RequestMapping({"${server.error.path:${error.path:/error}}"})
    public <K> ApiUnifiedResult<K> error(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        return ApiUnifiedResult.getErrorResult(status.value(), status.getReasonPhrase());
    }
    
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (Objects.isNull(statusCode)) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception e) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }

}

