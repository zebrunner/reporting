package com.qaprosoft.zafira;

import com.qaprosoft.zafira.ws.util.dozer.NullSafeDozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Arrays;

@ComponentScan
@ImportResource(value = {"classpath:zafira-models.xml", "classpath:zafira-app-dbaccess.xml", "classpath:zafira-services.xml"})
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
