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
package com.qaprosoft.zafira.dbaccess;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.qaprosoft.zafira.dbaccess.utils.TenancyDataSourceWrapper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.beans.PropertyVetoException;

@Configuration
public class PersistenceConfig {

    private static final String APP_SQL_SESSION_FACTORY_BEAN_NAME = "applicationSqlSessionFactory";
    private static final String APP_MAPPERS_BASE_PACKAGE = "com.qaprosoft.zafira.dbaccess.dao.mysql.application";

    private static final String MNG_SQL_SESSION_FACTORY_BEAN_NAME = "managementSqlSessionFactory";
    private static final String MNG_MAPPERS_BASE_PACKAGE = "com.qaprosoft.zafira.dbaccess.dao.mysql.management";

    @Bean
    public ComboPooledDataSource appDataSource(
            @Value("${zafira.db.jdbc.driverClass}") String driverClass,
            @Value("${zafira.db.jdbc.url}") String jdbcUrl,
            @Value("${zafira.db.jdbc.user}") String dbUsername,
            @Value("${zafira.db.jdbc.password}") String dbPassword,
            @Value("${zafira.db.c3p0.maxPoolSize}") int maxPoolSize,
            @Value("${zafira.db.c3p0.idleConnectionTestPeriod}") int idleConnectionTestPeriod
    ) throws PropertyVetoException {
        return buildDataSource(driverClass, jdbcUrl, dbUsername, dbPassword, maxPoolSize, idleConnectionTestPeriod);
    }

    @Bean
    public ComboPooledDataSource managementDataSource(
            @Value("${zafira.db.jdbc.driverClass}") String driverClass,
            @Value("${zafira.db.jdbc.url}") String jdbcUrl,
            @Value("${zafira.db.jdbc.user}") String dbUsername,
            @Value("${zafira.db.jdbc.password}") String dbPassword,
            @Value("${zafira.db.c3p0.maxPoolSize}") int maxPoolSize,
            @Value("${zafira.db.c3p0.idleConnectionTestPeriod}") int idleConnectionTestPeriod
    ) throws PropertyVetoException {
        ComboPooledDataSource dataSource = buildDataSource(driverClass, jdbcUrl, dbUsername, dbPassword, maxPoolSize, idleConnectionTestPeriod);
        dataSource.setIdentityToken("management");
        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(TenancyDataSourceWrapper tenancyAppDSWrapper) {
        return new DataSourceTransactionManager(tenancyAppDSWrapper.getDataSource());
    }

    @Bean
    public DataSourceTransactionManager managementTransactionManager(TenancyDataSourceWrapper tenancyMngDSWrapper) {
        return new DataSourceTransactionManager(tenancyMngDSWrapper.getDataSource());
    }

    @Bean
    public SqlSessionFactoryBean applicationSqlSessionFactory(
            TenancyDataSourceWrapper tenancyAppDSWrapper,
            @Value("classpath*:/com/qaprosoft/zafira/dbaccess/dao/mappers/application/**/*.xml") Resource[] appMapperResources
    ) {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(tenancyAppDSWrapper.getDataSource());
        sessionFactoryBean.setMapperLocations(appMapperResources);
        return sessionFactoryBean;
    }

    @Bean
    public SqlSessionFactoryBean managementSqlSessionFactory(
            TenancyDataSourceWrapper tenancyMngDSWrapper,
            @Value("classpath*:/com/qaprosoft/zafira/dbaccess/dao/mappers/management/**/*.xml") Resource[] managementMapperResources
    ) {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(tenancyMngDSWrapper.getDataSource());
        sessionFactoryBean.setMapperLocations(managementMapperResources);
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
    public MapperScannerConfigurer managementMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(MNG_MAPPERS_BASE_PACKAGE);
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(MNG_SQL_SESSION_FACTORY_BEAN_NAME);
        return mapperScannerConfigurer;
    }

    @Bean
    public TenancyDataSourceWrapper tenancyAppDSWrapper(ComboPooledDataSource appDataSource) {
        return new TenancyDataSourceWrapper(appDataSource);
    }

    @Bean
    public TenancyDataSourceWrapper tenancyMngDSWrapper(ComboPooledDataSource managementDataSource) {
        return new TenancyDataSourceWrapper(managementDataSource);
    }

    private ComboPooledDataSource buildDataSource(String driverClass, String jdbcUrl, String dbUsername,
                                                  String dbPassword, int maxPoolSize, int idleConnectionTestPeriod)
            throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
        return dataSource;
    }

}
