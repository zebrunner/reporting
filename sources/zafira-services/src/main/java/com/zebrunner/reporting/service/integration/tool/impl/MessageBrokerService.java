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
package com.zebrunner.reporting.service.integration.tool.impl;

import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.AbstractIntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.messagebroker.MessageBrokerAdapter;
import com.zebrunner.reporting.service.integration.tool.proxy.MessageBrokerProxy;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageBrokerService extends AbstractIntegrationService<MessageBrokerAdapter> {

    private final Map<String, Queue> queues;

    public MessageBrokerService(IntegrationService integrationService, MessageBrokerProxy messageBrokerProxy, Map<String, Queue> queues) {
        super(integrationService, messageBrokerProxy, "RABBITMQ");
        this.queues = queues;
    }

    public String getSettingQueueName() {
        return queues.get("settingsQueue").getName();
    }
}
