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
package com.zebrunner.reporting.service.util;

import com.zebrunner.reporting.domain.push.events.EventMessage;
import com.zebrunner.reporting.service.integration.tool.impl.MessageBrokerService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class EventPushService<T extends EventMessage> {

    private static final String EXCHANGE_NAME = "events";
    private static final String SUPPLIER_QUEUE_NAME_HEADER = "SUPPLIER_QUEUE";

    private final RabbitTemplate rabbitTemplate;
    private final MessageBrokerService messageBrokerService;

    public EventPushService(RabbitTemplate rabbitTemplate,
                            @Lazy MessageBrokerService messageBrokerService) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageBrokerService = messageBrokerService;
    }

    public enum Type {

        SETTINGS("settings"),
        ZFR_CALLBACKS("zfr_callbacks"),
        ZBR_EVENTS("zbr_events"),
        TENANCIES("tenancies");

        private final String routingKey;

        Type(String routingKey) {
            this.routingKey = routingKey;
        }

        public String getRoutingKey() {
            return routingKey;
        }
    }

    public boolean convertAndSend(Type type, T eventMessage) {
        return convertAndSend(type, eventMessage, setSupplierQueueNameHeader());
    }

    public boolean convertAndSend(Type type, T eventMessage, String headerName, String headerValue) {
        return convertAndSend(type, eventMessage, message -> {
            message.getMessageProperties().setHeader(headerName, headerValue);
            return message;
        });
    }

    private boolean convertAndSend(Type type, T eventMessage, MessagePostProcessor messagePostProcessor) {
        try {
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, type.getRoutingKey(), eventMessage, messagePostProcessor);
            return true;
        } catch (AmqpException e) {
            return false;
        }
    }

    private MessagePostProcessor setSupplierQueueNameHeader() {
        return message -> {
            message.getMessageProperties().getHeaders().putIfAbsent(SUPPLIER_QUEUE_NAME_HEADER, messageBrokerService.getSettingQueueName());
            return message;
        };
    }


    public boolean isSettingQueueConsumer(Message message) {
        return messageBrokerService.getSettingQueueName().equals(getSupplierQueueNameHeader(message));
    }

    private String getSupplierQueueNameHeader(Message message) {
        Object supplier =  message.getMessageProperties().getHeaders().get(SUPPLIER_QUEUE_NAME_HEADER);
        return supplier != null ? message.getMessageProperties().getHeaders().get(SUPPLIER_QUEUE_NAME_HEADER).toString() : null;
    }

}
