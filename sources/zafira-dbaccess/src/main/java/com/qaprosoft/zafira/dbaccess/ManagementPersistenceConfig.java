package com.qaprosoft.zafira.dbaccess;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.qaprosoft.zafira.dbaccess.utils.TenancyDataSourceWrapper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

//@Configuration
@MapperScan(basePackages = {"com.qaprosoft.zafira.dbaccess.dao.mysql.management"}, sqlSessionFactoryRef = "managementSqlSessionFactory")
@EnableTransactionManagement
//@PropertySource("classpath:environment.properties")
public class ManagementPersistenceConfig {

    private static final String MNG_SQL_SESSION_FACTORY_BEAN_NAME = "managementSqlSessionFactory";
    private static final String MNG_MAPPERS_BASE_PACKAGE = "com.qaprosoft.zafira.dbaccess.dao.mysql.management";

    @Autowired
    private Environment environment;

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

    @Value("classpath*:/com/qaprosoft/zafira/dbaccess/dao/mappers/management/**/*.xml")
    private Resource[] managementMapperResources;

    @Bean
    public ComboPooledDataSource managementDataSource() throws PropertyVetoException {
        String driverClass = environment.getProperty("zafira.db.jdbc.driverClass");
        driverClass.toString();
        ComboPooledDataSource dataSource = buildDataSource();
        dataSource.setIdentityToken("management");
        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager managementTransactionManager() throws PropertyVetoException {
        return new DataSourceTransactionManager(getManagementDataSource());
    }

    @Bean(name = MNG_SQL_SESSION_FACTORY_BEAN_NAME)
    public SqlSessionFactoryBean managementSqlSessionFactory() throws PropertyVetoException {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(getManagementDataSource());
        sessionFactoryBean.setMapperLocations(managementMapperResources);
        return sessionFactoryBean;
    }

    @Bean
    public MapperScannerConfigurer managementMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(MNG_MAPPERS_BASE_PACKAGE);
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(MNG_SQL_SESSION_FACTORY_BEAN_NAME);
        return mapperScannerConfigurer;
    }

    @Bean
    public TenancyDataSourceWrapper tenancyMngDSWrapper() throws PropertyVetoException {
        return new TenancyDataSourceWrapper(managementDataSource());
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

    private DataSource getManagementDataSource() throws PropertyVetoException {
        return tenancyMngDSWrapper().getDataSource();
    }

}
