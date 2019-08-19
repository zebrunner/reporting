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
package com.qaprosoft.zafira.dbaccess.utils;

import com.qaprosoft.zafira.dbaccess.dao.mysql.management.TenancyMapper;
import com.qaprosoft.zafira.models.db.Tenancy;
import liquibase.integration.spring.MultiTenantSpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "db-state-management.enabled", havingValue = "true")
public class DbStateManager {

    private static final String CHANGE_LOG_PATH = "classpath:db/changelog.yml";

    private final TenancyMapper tenancyMapper;
    private final boolean manageSpecificTenantsOnly;
    private final List<String> managedTenants;
    private final boolean manageSpecificLabelsOnly;
    private final String managedLabelsExpression;

    public DbStateManager(
            TenancyMapper tenancyMapper,
            @Value("${db-state-management.tenants.manage-specific-tenants-only:false}") boolean manageSpecificTenantsOnly,
            @Value("${db-state-management.tenants.managed-tenants}") List<String> managedTenants,
            @Value("${db-state-management.labels.manage-specific-labels-only:false}") boolean manageSpecificLabelsOnly,
            @Value("${db-state-management.labels.managed-labels-expression}") String manageLabelsExpression
            ) {
        this.tenancyMapper = tenancyMapper;
        this.manageSpecificTenantsOnly = manageSpecificTenantsOnly;
        this.managedTenants = managedTenants;
        this.manageSpecificLabelsOnly = manageSpecificLabelsOnly;
        this.managedLabelsExpression = manageLabelsExpression;
    }

    @Bean
    public MultiTenantSpringLiquibase liquibase(TenancyDataSourceWrapper tenancyAppDSWrapper) {
        List<String> tenancies = manageSpecificTenantsOnly ? managedTenants : getAllTenantNames();
        MultiTenantSpringLiquibase liquibase = new MultiTenantSpringLiquibase();
        liquibase.setDataSource(tenancyAppDSWrapper.getDataSource());
        liquibase.setSchemas(tenancies);
        liquibase.setChangeLog(CHANGE_LOG_PATH);

        if (manageSpecificLabelsOnly) {
            liquibase.setLabels(managedLabelsExpression);
        }
        return liquibase;
    }

    private List<String> getAllTenantNames() {
        return tenancyMapper.getAllTenancies().stream()
                            .map(Tenancy::getName)
                            .collect(Collectors.toCollection((Supplier<TenancyList<String>>) TenancyList::new));
    }

    private static class TenancyList<E> extends ArrayList<E> {

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {

                private final Iterator<E> iterator = TenancyList.super.iterator();

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public E next() {
                    E entity = iterator.next();
                    TenancyContext.setTenantName(entity.toString());
                    return entity;
                }
            };
        }
    }

}
