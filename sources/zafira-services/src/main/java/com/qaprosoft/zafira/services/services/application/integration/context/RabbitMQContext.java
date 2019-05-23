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
package com.qaprosoft.zafira.services.services.application.integration.context;

import com.qaprosoft.zafira.models.db.Setting;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RabbitMQContext extends AbstractContext {

    private final CachingConnectionFactory cachingConnectionFactory;
    private Connection connection;
    private CompletableFuture<Connection> connectionCompletableFuture;

    public RabbitMQContext(Map<Setting.SettingType, String> settings) {
        super(settings, settings.get(Setting.SettingType.RABBITMQ_ENABLED));

        String host = settings.get(Setting.SettingType.RABBITMQ_HOST);
        String port = settings.get(Setting.SettingType.RABBITMQ_PORT);
        String username = settings.get(Setting.SettingType.RABBITMQ_USER);
        String password = settings.get(Setting.SettingType.RABBITMQ_PASSWORD);

        this.cachingConnectionFactory = new CachingConnectionFactory(host, Integer.parseInt(port));
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

    /**
     * Gets new connection from RabbitMQ connections factory.
     * If connection is not established, tries to retrieve it from factory using
     * previously instantiated connection completable future or creates a completable future if it doesn't exist.
     * Compleatble future interrupts after 15 seconds if completable future does not completed yet
     * 
     * @return retrieved connection
     */
    public Connection getConnection() {
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

}
