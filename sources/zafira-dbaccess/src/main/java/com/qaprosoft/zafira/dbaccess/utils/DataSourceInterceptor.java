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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * DataSourceInterceptor - proxies data sources calls for custom interceptors.
 * 
 * @author akhursevich
 */
public abstract class DataSourceInterceptor {

	private final InvocationHandler handler;

	protected DataSourceInterceptor(final ComboPooledDataSource delegate) {
		this.handler = new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return (method.getName().equals("getConnection")) ? getConnection(delegate) : method.invoke(delegate, args);
			}
		};
	}

	protected Connection getConnection(final ComboPooledDataSource delegate) throws SQLException {
		return delegate.getConnection();
	}

	public static DataSource wrapInterceptor(DataSourceInterceptor instance) {
		return (DataSource) Proxy.newProxyInstance(instance.getClass().getClassLoader(), new Class[] { DataSource.class }, instance.handler);
	}
}
