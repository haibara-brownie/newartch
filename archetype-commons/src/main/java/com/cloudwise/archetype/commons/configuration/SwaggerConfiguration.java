package com.cloudwise.archetype.commons.configuration;

import cn.hutool.extra.spring.SpringUtil;
import com.deepoove.swagger.dubbo.annotations.EnableDubboSwagger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@EnableSwagger2
@Configuration
@EnableDubboSwagger
public class SwaggerConfiguration {

    @Bean
    public Docket createRestAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select().apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(StringUtils.defaultIfBlank(SpringUtil.getApplicationName(), "swagger-title"))
                .description(StringUtils.EMPTY).termsOfServiceUrl(StringUtils.EMPTY)
                .contact(new Contact(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY))
                .version("1.0.0")
                .build();
    }

    @Bean
    @Primary
    public SwaggerResourcesProvider newSwaggerResourcesProvider(Environment env, DocumentationCache documentationCache, DocumentationPluginsManager pluginsManager) {
        return new InMemorySwaggerResourcesProvider(env, documentationCache, pluginsManager) {

            @Override
            public List<SwaggerResource> get() {
                // 1. 调用 InMemorySwaggerResourcesProvider
                List<SwaggerResource> resources = super.get();
                // 2. 添加 swagger-dubbo 的资源地址
                SwaggerResource dubboSwaggerResource = new SwaggerResource();
                dubboSwaggerResource.setName("dubbo");
                dubboSwaggerResource.setSwaggerVersion("2.0");
                dubboSwaggerResource.setUrl("/swagger-dubbo/api-docs");
                resources.add(0, dubboSwaggerResource);
                return resources;
            }

        };
    }
}