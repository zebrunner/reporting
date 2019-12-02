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
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.service.bean.RabbitMQConfigBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String[] STOMP_ENDPOINTS = new String[]{"/api/websockets"};
    private static final String[] STOMP_BROKER_DESTINATIONS = new String[]{"/topic"};

    private final RabbitMQConfigBean rabbitMQConfigBean;

    public WebSocketConfig(RabbitMQConfigBean rabbitMQConfigBean) {
        this.rabbitMQConfigBean = rabbitMQConfigBean;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint(STOMP_ENDPOINTS)
                             .setAllowedOrigins("*")
                             .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        messageBrokerRegistry.enableStompBrokerRelay(STOMP_BROKER_DESTINATIONS)
                             .setRelayHost(rabbitMQConfigBean.getHost())
                             .setRelayPort(rabbitMQConfigBean.getPort())
                             .setClientLogin(rabbitMQConfigBean.getClientUser())
                             .setClientPasscode(rabbitMQConfigBean.getClientPasscode())
                             .setSystemLogin(rabbitMQConfigBean.getSystemUser())
                             .setSystemPasscode(rabbitMQConfigBean.getSystemPasscode());
    }

}
