package com.qaprosoft.zafira.ws;

import com.qaprosoft.zafira.ws.security.filter.CORSFilter;
import com.qaprosoft.zafira.ws.security.filter.TenancyFilter;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.io.IOException;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer/* implements ApplicationContextInitializer*/ {

    /*@Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(WebConfig.class);
        webContext.setServletContext(servletContext);
        servletContext.addListener(new ContextLoaderListener(webContext));
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(webContext));
        servlet.setLoadOnStartup(1);
        servlet.setAsyncSupported(true);
        servlet.addMapping("/");
    }*/

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] {RootConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] {WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }

    // TODO: 2019-07-16 tenancy filter ? filter mappings? move to security initializer?
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return new Filter[] {
                new DelegatingFilterProxy(),
                characterEncodingFilter,
                new CORSFilter(),
                new TenancyFilter()
        };
    }

    /*@Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Resource resource = new ClassPathResource("environment.properties");
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        MutablePropertySources mps = env.getPropertySources();
        try {
            mps.addFirst(new ResourcePropertySource("config-file", resource));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected ApplicationContextInitializer<?>[] getRootApplicationContextInitializers() {
        return new ApplicationContextInitializer[] {new AppInitializer()};
    }

    @Override
    protected ApplicationContextInitializer<?>[] getServletApplicationContextInitializers() {
        return new ApplicationContextInitializer[] {new AppInitializer()};
    }*/

}
