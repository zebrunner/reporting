/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.dbaccess.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.qaprosoft.zafira.models.db.management.Tenancy;

/**
 * TenancyDataSourceWrapper - initializes schema according to current tenant.
 * 
 * @author akhursevich
 */
public class TenancyDataSourceWrapper {

	private DataSource dataSource;
	
	private static final String SET_SEARCH_PATH_SQL = "SET search_path TO '%s';";

	protected TenancyDataSourceWrapper(ComboPooledDataSource ds) {
		this.dataSource = DataSourceInterceptor.wrapInterceptor(new DataSourceInterceptor(ds) {
			@Override
			protected Connection getConnection(ComboPooledDataSource delegate) throws SQLException {
				Connection connection = delegate.getConnection();
				String schema = getSchema(delegate);
				connection.prepareStatement(String.format(SET_SEARCH_PATH_SQL, schema)).execute();
				return connection;
			}
		});
	}

	public DataSource getDataSource() {
		return this.dataSource;
	}

	private static String getSchema(ComboPooledDataSource delegate) {
		return delegate.getIdentityToken().equals(Tenancy.getManagementSchema()) || TenancyContext.getTenantName().equals(Tenancy.getManagementSchema()) ?
				Tenancy.getManagementSchema() : TenancyContext.getTenantName();
	}
}
