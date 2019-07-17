package com.qaprosoft.zafira.dbaccess;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.qaprosoft.zafira.dbaccess.utils.TenancyDataSourceWrapper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

//@Configuration
@MapperScan(basePackages = {"com.qaprosoft.zafira.dbaccess.dao.mysql.application"}, sqlSessionFactoryRef = "applicationSqlSessionFactory")
@EnableTransactionManagement
//@PropertySource("classpath:environment.properties")
public class AppPersistenceConfig {

    private static final String APP_SQL_SESSION_FACTORY_BEAN_NAME = "applicationSqlSessionFactory";

    private static final String APP_MAPPERS_BASE_PACKAGE = "com.qaprosoft.zafira.dbaccess.dao.mysql.application";

    @Value("${zafira.db.jdbc.driverClass}")
    private String driverClass;

    @Value("${zafira.db.jdbc.url}")
    private String jdbcUrl;

    @Value("${zafira.db.jdbc.user}")
    private String dbUsername;

    @Value("${zafira.db.jdbc.password}")
    private String dbPassword;

    @Value("${zafira.db.c3p0.maxPoolSize}")
    private int maxPoolSize;

    @Value("${zafira.db.c3p0.idleConnectionTestPeriod}")
    private int idleConnectionTestPeriod;

    @Value("classpath*:/com/qaprosoft/zafira/dbaccess/dao/mappers/application/**/*.xml")
    private Resource[] appMapperResources;

    @Bean
    public ComboPooledDataSource appDataSource() throws PropertyVetoException {
        return buildDataSource();
    }

    @Bean
    public DataSourceTransactionManager transactionManager() throws PropertyVetoException {
        return new DataSourceTransactionManager(getAppDataSource());
    }

    @Bean(name = APP_SQL_SESSION_FACTORY_BEAN_NAME)
    public SqlSessionFactoryBean applicationSqlSessionFactory() throws PropertyVetoException {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(getAppDataSource());
        sessionFactoryBean.setMapperLocations(appMapperResources);
        return sessionFactoryBean;
    }

    @Bean
    public MapperScannerConfigurer appMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(APP_MAPPERS_BASE_PACKAGE);
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(APP_SQL_SESSION_FACTORY_BEAN_NAME);
        return mapperScannerConfigurer;
    }

    @Bean
    public TenancyDataSourceWrapper tenancyAppDSWrapper() throws PropertyVetoException {
        return new TenancyDataSourceWrapper(appDataSource());
    }

    private ComboPooledDataSource buildDataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
        return dataSource;
    }

    private DataSource getAppDataSource() throws PropertyVetoException {
        return tenancyAppDSWrapper().getDataSource();
    }

}
