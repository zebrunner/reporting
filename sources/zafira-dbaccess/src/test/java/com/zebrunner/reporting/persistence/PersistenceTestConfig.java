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
package com.zebrunner.reporting.persistence;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zebrunner.reporting.persistence.utils.TenancyDataSourceWrapper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.beans.PropertyVetoException;

@PropertySource("db.properties")
public class PersistenceTestConfig {

    private static final String SESSION_FACTORY_BEAN_NAME = "sqlSessionFactory";
    private static final String MAPPERS_BASE_PACKAGE = "com.qaprosoft.zafira.dbaccess.dao.mysql";

    @Bean
    public ComboPooledDataSource dataSource(
            @Value("${db.jdbc.driverClass}") String driverClass,
            @Value("${db.jdbc.url}") String jdbcUrl,
            @Value("${db.jdbc.user}") String dbUsername,
            @Value("${db.jdbc.password}") String dbPassword,
            @Value("${db.c3p0.maxPoolSize}") int maxPoolSize
    ) throws PropertyVetoException {
        return buildDataSource(driverClass, jdbcUrl, dbUsername, dbPassword, maxPoolSize);
    }

    @Bean
    public DataSourceTransactionManager transactionManager(TenancyDataSourceWrapper tenancyAppDSWrapper) {
        return new DataSourceTransactionManager(tenancyAppDSWrapper.getDataSource());
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(
            TenancyDataSourceWrapper tenancyAppDSWrapper,
            @Value("classpath*:/com/qaprosoft/zafira/dbaccess/dao/mappers/**/*.xml") Resource[] appMapperResources
    ) {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(tenancyAppDSWrapper.getDataSource());
        sessionFactoryBean.setMapperLocations(appMapperResources);
        return sessionFactoryBean;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(MAPPERS_BASE_PACKAGE);
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(SESSION_FACTORY_BEAN_NAME);
        return mapperScannerConfigurer;
    }

    @Bean
    public TenancyDataSourceWrapper tenancyDataSourceWrapper(ComboPooledDataSource appDataSource) {
        return new TenancyDataSourceWrapper(appDataSource);
    }

    private ComboPooledDataSource buildDataSource(String driverClass, String jdbcUrl, String dbUsername,
                                                  String dbPassword, int maxPoolSize)
            throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setMaxPoolSize(maxPoolSize);
        return dataSource;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
