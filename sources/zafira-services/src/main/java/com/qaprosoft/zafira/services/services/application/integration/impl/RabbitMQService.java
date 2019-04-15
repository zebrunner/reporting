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
 *******************************************************************************/
package com.qaprosoft.zafira.services.services.application.integration.impl;

import static com.qaprosoft.zafira.models.db.Setting.Tool.RABBITMQ;

import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.Connection;

import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.context.RabbitMQContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RabbitMQService extends AbstractIntegration<RabbitMQContext> {

    private static final String SETTINGS_QUEUE_NAME = "settingsQueue";
    private final Map<String, Queue> queues;

    public RabbitMQService(SettingsService settingsService, CryptoService cryptoService, Map<String, Queue> queues) {
        super(settingsService, cryptoService, RABBITMQ, RabbitMQContext.class);
        this.queues = queues;
    }

    private Queue getQueueById(String id) {
        return queues.get(id);
    }

    public boolean isSettingQueueConsumer(String settingQueueName) {
        return settingQueueName.equals(getQueueById(SETTINGS_QUEUE_NAME).getName());
    }

    @Override
    public boolean isConnected() {
        try {
            Connection connection = context().getConnection();
            return connection != null && connection.isOpen();
        } catch (Exception e) {
            return false;
        }
    }

}
