package com.qaprosoft.zafira.ws;

import com.qaprosoft.zafira.ws.util.dozer.NullSafeDozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;

//@PropertySource("classpath:environment.properties")
@ComponentScan(basePackages = {"com.qaprosoft.zafira"})
@EnableWebMvc
@ImportResource(value = {"classpath:zafira-models.xml", "classpath:zafira-app-dbaccess.xml", "classpath:zafira-mng-dbaccess.xml", "classpath:zafira-services.xml"})
//@Import(value = {ManagementPersistenceConfig.class})
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
    public PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        placeholderConfigurer.setLocation(new ClassPathResource("environment.properties"));
        return placeholderConfigurer;
    }

    @Bean
    public NullSafeDozerBeanMapper mapper() {
        NullSafeDozerBeanMapper dozerBeanMapper = new NullSafeDozerBeanMapper();
        dozerBeanMapper.setMappingFiles(Arrays.asList(DOZER_MAPPING_FILES));
        return dozerBeanMapper;
    }

}
