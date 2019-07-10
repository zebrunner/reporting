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
package com.qaprosoft.zafira.log.event.impl;

import com.qaprosoft.zafira.client.BasicClient;
import com.qaprosoft.zafira.log.event.AmqpService;
import com.qaprosoft.zafira.log.event.EventPublisher;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.util.http.HttpClient;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.qaprosoft.zafira.models.db.Setting.Tool.RABBITMQ;

public class RabbitMQService implements AmqpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQService.class);

    private static final String VIRTUAL_HOST = "/";
    private static final String TYPE = "x-recent-history";
    private static final String EXCHANGE_NAME = "logs";
    private static final int HISTORY = 1000;

    private final BasicClient client;

    private ConnectionFactory factory;
    private Connection connection = null;
    private Channel channel = null;

    private String host;
    private int port;
    private String username;
    private String password;
    private boolean connected;

    public RabbitMQService(BasicClient client) {
        this.client = client;
        this.host = "localhost";
        this.port = 5672;
        this.username = "guest";
        this.password = "guest";
    }

    //TODO: 2019-07-04 synchronize?
    @Override
    public synchronized EventPublisher connect() throws IOException, TimeoutException {
        boolean isRabbitMQEnabled = initAuthProperties();
        if(isRabbitMQEnabled) {
            this.factory = createConnectionFactory();
            this.connection = createConnection(this.factory);
            this.channel = createChannel(this.connection);
            declareExchange(this.channel);
        }
        return new EventPublisherImpl(this.channel);
    }

    @Override
    public void releaseConnection() throws IOException, TimeoutException {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }

        if (connection != null && connection.isOpen()) {
            this.connection.close();
        }
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    public class EventPublisherImpl implements EventPublisher {

        private final Channel channel;

        EventPublisherImpl(Channel channel) {
            this.channel = channel;
        }

        public boolean publishEvent(String routingKey, String correlationId, String appId, String eventType, String payload) {
            boolean result = false;
            AMQP.BasicProperties.Builder b = new AMQP.BasicProperties().builder()
                                                                       .appId(appId)
                                                                       .type(eventType)
                                                                       .correlationId(String.valueOf(correlationId))
                                                                       .contentType("text/json");
            try {
                this.channel.basicPublish(EXCHANGE_NAME, routingKey, b.build(), payload.getBytes());
                result = true;
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
            return result;
        }

    }

    private ConnectionFactory createConnectionFactory() {
        ConnectionFactory factory = this.factory;
        if(factory == null) {
            factory = new ConnectionFactory();
            factory.setHost(this.host);
            factory.setPort(this.port);
            factory.setVirtualHost(VIRTUAL_HOST);
            factory.setUsername(this.username);
            factory.setPassword(this.password);
        }
        return factory;
    }

    /**
     * Creates connection to RabbitMQ server according to properties
     *
     * @return connection
     */
    private Connection createConnection(ConnectionFactory factory) throws IOException, TimeoutException {
        Connection connection = this.connection;
        if (connection == null || !connection.isOpen()) {
            try {
                connection = factory.newConnection();
            } catch (IOException e) {
                throw new IOException("Unable to create RabbitMQ connection: " + e.getMessage(), e);
            } catch (TimeoutException e) {
                throw new TimeoutException("Connection cannot be established: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Creates channel on RabbitMQ server
     *
     * @return channel
     */
    private Channel createChannel(Connection connection) throws IOException {
        Channel channel = this.channel;
        if (channel == null || !channel.isOpen() && (connection != null && connection.isOpen())) {
            try {
                channel = connection.createChannel();
            } catch (IOException e) {
                throw new IOException("Unable to create RabbitMQ channel: " + e.getMessage(), e);
            }
        }
        return channel;
    }

    /**
     * Declares the exchange on RabbitMQ server according to properties set
     */
    private void declareExchange(Channel channel) throws IOException {
        if (channel != null && channel.isOpen()) {
            try {
                Map<String, Object> args = new HashMap<>();
                args.put(TYPE, HISTORY);
                channel.exchangeDeclare(EXCHANGE_NAME, "x-recent-history", false, false, args);
            } catch (IOException e) {
                throw new IOException("Unable to create RabbitMQ exchange: " + e.getMessage(), e);
            }
        }
    }

    private boolean initAuthProperties() {
        if(client != null) {
            HttpClient.Response<List<HashMap<String, String>>> rs = client.getToolSettings(RABBITMQ.name(), true);
            if (rs.getStatus() == 200) {
                List<HashMap<String, String>> settings = rs.getObject();
                if (settings != null) {
                    settings.forEach(this::initAuthProperty);
                }
            }
        }
        return connected;
    }

    private void initAuthProperty(HashMap<String, String> settings) {
        Setting.SettingType settingType = Setting.SettingType.valueOf(settings.get("name"));
        switch (settingType) {
            case RABBITMQ_HOST:
                this.host = settings.get("value");
                break;
            case RABBITMQ_PORT:
                this.port = Integer.valueOf(settings.get("value"));
                break;
            case RABBITMQ_USER:
                this.username = settings.get("value");
                break;
            case RABBITMQ_PASSWORD:
                this.password = settings.get("value");
                break;
            case RABBITMQ_ENABLED:
                this.connected = Boolean.valueOf(settings.get("value"));
                break;
            default:
                break;
        }
    }

}
