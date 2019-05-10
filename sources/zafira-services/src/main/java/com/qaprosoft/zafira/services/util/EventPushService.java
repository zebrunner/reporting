/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
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
package com.qaprosoft.zafira.services.util;

import com.qaprosoft.zafira.models.push.events.EventMessage;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EventPushService<T extends EventMessage> {

    private static final String EXCHANGE_NAME = "events";

    @Autowired
    @Qualifier("eventsTemplate")
    private RabbitTemplate rabbitTemplate;

    public enum Type {

        SETTINGS("settings"), MONITORS("monitors"), ZFR_CALLBACKS("zfr_callbacks"), TENANCIES("tenancies");

        private final String routingKey;

        Type(String routingKey) {
            this.routingKey = routingKey;
        }

        public String getRoutingKey() {
            return routingKey;
        }
    }

    public boolean convertAndSend(Type type, T eventMessage) {
        try {
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, type.getRoutingKey(), eventMessage);
            return true;
        } catch (AmqpException e) {
            return false;
        }
    }

    public boolean convertAndSend(Type type, T eventMessage, String headerName, String headerValue) {
        try {
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, type.getRoutingKey(), eventMessage, message -> {
                message.getMessageProperties().setHeader(headerName, headerValue);
                return message;
            });
            return true;
        } catch (AmqpException e) {
            return false;
        }
    }
}
