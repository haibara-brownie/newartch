package com.cloudwise.archetype.commons.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "服务探活相关")
@RestController
@RequestMapping
public class HealthController {

    private static final String SUCCESS_DATA = "root";

    @ApiOperation(value = "探活接口服务", notes = "探活接口服务")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = String.class)
    })
    @GetMapping({"/", "/health"})
    public String getRoot() {
        return SUCCESS_DATA;
    }

}