/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
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
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.jmx.models;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;

import java.util.concurrent.CompletableFuture;

public class RabbitMQType extends AbstractType
{

    private CachingConnectionFactory cachingConnectionFactory;
    private Connection connection;
    private CompletableFuture<Connection> connectionCompletableFuture;

    public RabbitMQType(String host, String port, String username, String password)
    {
        this.cachingConnectionFactory = new CachingConnectionFactory(host, Integer.parseInt(port));
        this.cachingConnectionFactory.setUsername(username);
        this.cachingConnectionFactory.setPassword(password);
        this.connectionCompletableFuture = CompletableFuture.supplyAsync(() -> {
            this.connection = this.cachingConnectionFactory.createConnection();
            return this.connection;
        });
        this.cachingConnectionFactory.addConnectionListener(new ConnectionListener() {
            @Override public void onCreate(Connection connection) {
                try {
                    connectionCompletableFuture.complete(connection);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            @Override public void onClose(Connection connection) {
            }
        });
    }

    public CompletableFuture getConnectionCompletableFuture() {
        return CompletableFuture.allOf(this.connectionCompletableFuture);
    }

    public CachingConnectionFactory getCachingConnectionFactory()
    {
        return cachingConnectionFactory;
    }

    public void setCachingConnectionFactory(CachingConnectionFactory cachingConnectionFactory)
    {
        this.cachingConnectionFactory = cachingConnectionFactory;
    }

    public Connection getConnection()
    {
        return connection;
    }

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }
}
