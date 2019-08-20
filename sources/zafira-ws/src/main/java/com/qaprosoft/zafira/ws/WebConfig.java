/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.ws;

import com.qaprosoft.zafira.ws.util.dozer.NullSafeDozerBeanMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

@EnableAsync
@Configuration
@EnableSwagger2
public class WebConfig implements WebMvcConfigurer {

    private static final String BASENAME_LOCATION = "classpath:i18n/messages";

    private final boolean multitenant;

    public WebConfig(@Value("${zafira.multitenant}") boolean multitenant) {
        this.multitenant = multitenant;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(BASENAME_LOCATION);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        if (!multitenant) {
            // add extra resource handler to serve local resources - single host deployment only
            registry.addResourceHandler("/assets/**")
                    .addResourceLocations("file:/opt/assets/");
        }
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(validator());
        return methodValidationPostProcessor;
    }

    private static final String[] DOZER_MAPPING_FILES = new String[] {
            "dozer/Filter-dozer-mapping.xml",
            "dozer/Launcher-dozer-mapping.xml",
            "dozer/TestSuite-dozer-mapping.xml",
            "dozer/TestCase-dozer-mapping.xml",
            "dozer/Test-dozer-mapping.xml",
            "dozer/Job-dozer-mapping.xml",
            "dozer/TestRun-dozer-mapping.xml",
            "dozer/User-dozer-mapping.xml",
            "dozer/Project-dozer-mapping.xml",
            "dozer/TestArtifact-dozer-mapping.xml",
            "dozer/Tenancy-dozer-mapping.xml",
            "dozer/Invitation-dozer-mapping.xml",
            "dozer/ScmAccount-dozer-mapping.xml",
            "dozer/Tag-dozer-mapping.xml",
            "dozer/WidgetTemplate-dozer-mapping.xml"
    };

    @Bean
    public NullSafeDozerBeanMapper mapper() {
        NullSafeDozerBeanMapper beanMapper = new NullSafeDozerBeanMapper();
        beanMapper.setMappingFiles(Arrays.asList(DOZER_MAPPING_FILES));
        return beanMapper;
    }

    @Bean
    public Docket api(@Value("${zafira.debug-enabled:false}") boolean debugMode) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("zafira-api")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.qaprosoft.zafira.ws"))
                .paths(PathSelectors.any())
                .build()
                .enable(debugMode)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Zafira API")
                .description("Describes public and private endpoints behavior")
                .termsOfServiceUrl("http://springfox.io")
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
                .version("2.0")
                .build();
    }

    /**
     * Registers placeholder configurer to resolve properties
     * Order is required, `cause  there is at least one placeholder configurer in servlet context by default.
     * Order is necessary to resolve their conflicts
     * @return a created placeholder configurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        placeholderConfigurer.setOrder(Integer.MIN_VALUE);
        return placeholderConfigurer;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

}
