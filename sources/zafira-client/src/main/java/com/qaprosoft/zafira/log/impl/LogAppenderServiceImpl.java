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
package com.qaprosoft.zafira.log.impl;

import com.qaprosoft.zafira.client.ZafiraSingleton;
import com.qaprosoft.zafira.log.BaseAppenderTask;
import com.qaprosoft.zafira.log.event.AmqpService;
import com.qaprosoft.zafira.log.event.EventPublisher;
import com.qaprosoft.zafira.log.event.impl.RabbitMQService;
import com.qaprosoft.zafira.log.LogAppenderService;
import com.qaprosoft.zafira.util.ConfigurationUtil;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * @author akhursevich
 */
public class LogAppenderServiceImpl implements LogAppenderService {

    private static final ZafiraSingleton ZAFIRA_INSTANCE = ZafiraSingleton.INSTANCE;
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    private final AmqpService amqpService;

    private EventPublisher eventPublisher;
    private String identifier = null;
    private boolean durable = false;
    private String routingKey = "";
    private boolean zafiraConnected = false;

    public LogAppenderServiceImpl() {
        this.amqpService = new RabbitMQService(ZAFIRA_INSTANCE.getClient());
    }

    @Override
    public void append(BaseAppenderTask task) {
        task.setRoutingKey(routingKey);
        task.setEventPublisher(eventPublisher);
        task.setIdentifier(identifier);
        task.setZafiraConnected(zafiraConnected);
        threadPool.submit(task);
    }

    /**
     * Connects to Zafira API and retrieves RabbitMQ configuration.
     */
    @Override
    public void connectZafira() throws IOException, TimeoutException {
        CombinedConfiguration config = ConfigurationUtil.getConfiguration();
        if (ZAFIRA_INSTANCE.isRunning()) {
            // Queue referenced to ci_run_id
            this.routingKey = getRoutingKey(config);
            this.eventPublisher = amqpService.connect();
            this.zafiraConnected = amqpService.isConnected();
        }
    }

    @Override
    public void onClose() throws IOException, TimeoutException {
        if(amqpService != null) {
            amqpService.releaseConnection();
        }
    }

    /**
     * Returns identifier property as set in appender configuration
     * 
     * @return identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets identifier property from parameter in appender configuration
     * 
     * @param identifier - identifier property
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isDurable() {
        return durable;
    }

    /**
     * Sets durable property from parameter in appender configuration
     * 
     * @param durable - durable property
     */
    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    /**
     * Returns routingKey property as set in appender configuration
     * 
     * @return routingKey
     */
    public String getRoutingKey() {
        return routingKey;
    }

    private String getRoutingKey(ImmutableConfiguration configuration) {
        String routingKey = configuration.getString("ci_run_id", null);
        if (StringUtils.isEmpty(routingKey)) {
            routingKey = UUID.randomUUID().toString();
            System.setProperty("ci_run_id", routingKey);
        }
        return routingKey;
    }

}
