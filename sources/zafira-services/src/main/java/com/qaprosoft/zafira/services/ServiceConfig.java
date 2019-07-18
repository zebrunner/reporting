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
package com.qaprosoft.zafira.services;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
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

    @Bean
    public MessageSource serviceMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/services/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
