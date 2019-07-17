package com.qaprosoft.zafira.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Must be standalone cause must to have @Configuration annotation
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api(@Value("${zafira.debugMode:false}") boolean debugMode) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("zafira-api")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.qaprosoft.zafira.ws"))
                .paths(PathSelectors.ant("/**"))
                .build()
                .enable(debugMode)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring MVC swagger document")
                .description("End Points")
                .termsOfServiceUrl("http://springfox.io")
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
                .version("2.0")
                .build();
    }

}
