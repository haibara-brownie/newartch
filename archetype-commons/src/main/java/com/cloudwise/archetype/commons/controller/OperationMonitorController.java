package com.cloudwise.archetype.commons.controller;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@Data
@Slf4j
@RestController
@Api(tags = "运维监控-对外")
public class OperationMonitorController {
    private static final String SUCCESS_RESPONSE_TEXT = "okay";

    @GetMapping(value = "/healthy")
    @ApiOperation(value = "健康检查", tags = "5.3.1")
    public void healthy(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ServletUtil.write(response, SUCCESS_RESPONSE_TEXT, ContentType.TEXT_PLAIN.getValue());
    }

}
