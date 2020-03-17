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
 ******************************************************************************/
package com.zebrunner.reporting.service.integration.tool.adapter.messagebroker;

import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RabbitMQIntegrationAdapter extends AbstractIntegrationAdapter implements MessageBrokerAdapter {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    private final CachingConnectionFactory cachingConnectionFactory;
    private Connection connection;
    private CompletableFuture<Connection> connectionCompletableFuture;

    public RabbitMQIntegrationAdapter(Integration integration) {
        super(integration);

        this.host = getAttributeValue(integration, RabbitMQParam.RABBITMQ_HOST);
        this.port = Integer.parseInt(getAttributeValue(integration, RabbitMQParam.RABBITMQ_PORT));
        this.username = getAttributeValue(integration, RabbitMQParam.RABBITMQ_USERNAME);
        this.password = getAttributeValue(integration, RabbitMQParam.RABBITMQ_PASSWORD);

        this.cachingConnectionFactory = new CachingConnectionFactory(host, port);
        this.cachingConnectionFactory.setUsername(username);
        this.cachingConnectionFactory.setPassword(password);
        this.cachingConnectionFactory.addConnectionListener(new ConnectionListener() {
            @Override
            public void onCreate(Connection connection) {
                try {
                    connectionCompletableFuture.complete(connection);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            @Override
            public void onClose(Connection conn) {
                // connection = cachingConnectionFactory.createConnection();
            }
        });
    }

    private enum RabbitMQParam implements AdapterParam {
        RABBITMQ_HOST("RABBITMQ_HOST"),
        RABBITMQ_PORT("RABBITMQ_PORT"),
        RABBITMQ_USERNAME("RABBITMQ_USER"),
        RABBITMQ_PASSWORD("RABBITMQ_PASSWORD");

        private final String name;

        RabbitMQParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Gets new connection from RabbitMQ connections factory.
     * If connection is not established, tries to retrieve it from factory using
     * previously instantiated connection completable future or creates a completable future if it doesn't exist.
     * Compleatble future interrupts after 15 seconds if completable future does not completed yet
     * 
     * @return retrieved connection
     */
    private Connection getConnection() {
        if (connection == null || !connection.isOpen()) {
            if (connectionCompletableFuture == null || connectionCompletableFuture.isDone()) {
                this.connectionCompletableFuture = CompletableFuture.supplyAsync(() -> {
                    this.connection = this.cachingConnectionFactory.createConnection();
                    return this.connection;
                });
            }
            try {
                connection = connectionCompletableFuture.get(15, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return connection;
    }

    @Override
    public boolean isConnected() {
        try {
            return getConnection() != null && getConnection().isOpen();
        } catch (Exception e) {
            return false;
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public CachingConnectionFactory getCachingConnectionFactory() {
        return cachingConnectionFactory;
    }

    public CompletableFuture<Connection> getConnectionCompletableFuture() {
        return connectionCompletableFuture;
    }
}
