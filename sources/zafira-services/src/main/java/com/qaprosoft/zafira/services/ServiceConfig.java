package com.qaprosoft.zafira.services;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableAspectJAutoProxy
public class ServiceConfig {

    @Bean
    public PasswordEncryptor passwordEncryptor() {
        return new BasicPasswordEncryptor();
    }

    @Bean
    public FreeMarkerConfigurationFactoryBean freemarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean factoryBean = new FreeMarkerConfigurationFactoryBean();
        factoryBean.setPreferFileSystemAccess(Boolean.TRUE);
        factoryBean.setTemplateLoaderPaths("classpath:templates");
        factoryBean.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return factoryBean;
    }

}
