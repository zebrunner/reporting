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
package com.qaprosoft.zafira;

import com.qaprosoft.zafira.ws.util.dozer.NullSafeDozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Arrays;

@ComponentScan("com.qaprosoft.zafira")
@PropertySource("classpath:environment.properties")
public class RootConfig {

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
        NullSafeDozerBeanMapper dozerBeanMapper = new NullSafeDozerBeanMapper();
        dozerBeanMapper.setMappingFiles(Arrays.asList(DOZER_MAPPING_FILES));
        return dozerBeanMapper;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
