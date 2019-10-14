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
package com.qaprosoft.zafira.dbaccess.state;

import com.qaprosoft.zafira.dbaccess.dao.mysql.management.TenancyMapper;
import com.qaprosoft.zafira.dbaccess.utils.TenancyDataSourceWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class DatabaseStateManagerResolver {

    private final TenancyDataSourceWrapper tenancyAppDSWrapper;
    private final TenancyMapper tenancyMapper;
    private final boolean manageSpecificTenantsOnly;
    private final DatabaseStateManager.TenancyList<String> managedTenants;
    private final boolean manageSpecificLabelsOnly;
    private final String managedLabelsExpression;
    private final ResourceLoader resourceLoader;

    public DatabaseStateManagerResolver(
            TenancyMapper tenancyMapper,
            TenancyDataSourceWrapper tenancyAppDSWrapper,
            @Value("${db-state-management.manage-specific-tenants:false}") boolean manageSpecificTenantsOnly,
            @Value("${db-state-management.managed-tenants:#{T(java.util.Collections).emptyList()}}") DatabaseStateManager.TenancyList<String> managedTenants,
            @Value("${db-state-management.labels.enabled:false}") boolean manageSpecificLabelsOnly,
            @Value("${db-state-management.labels.managed-expression:@null}") String manageLabelsExpression,
            ResourceLoader resourceLoader
    ) {
        this.tenancyMapper = tenancyMapper;
        this.tenancyAppDSWrapper = tenancyAppDSWrapper;
        this.manageSpecificTenantsOnly = manageSpecificTenantsOnly;
        this.managedTenants = managedTenants;
        this.manageSpecificLabelsOnly = manageSpecificLabelsOnly;
        this.managedLabelsExpression = manageLabelsExpression;
        this.resourceLoader = resourceLoader;
    }

    @Bean("databaseStateManager")
    @ConditionalOnProperty(name = "db-state-management.enabled", havingValue = "true")
    public DatabaseStateManager databaseStateManager() {
        return new DatabaseStateManager(
                tenancyMapper,
                tenancyAppDSWrapper,
                manageSpecificTenantsOnly,
                managedTenants,
                manageSpecificLabelsOnly,
                managedLabelsExpression,
                resourceLoader
        );
    }

    @Bean("databaseStateManager")
    @ConditionalOnProperty(name = "db-state-management.enabled", havingValue = "false")
    public DatabaseStateManager databaseStateManagerDefault() {
        return null;
    }
}
