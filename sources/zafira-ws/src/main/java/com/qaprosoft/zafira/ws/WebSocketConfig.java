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
package com.qaprosoft.zafira.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    private static final String[] STOMP_ENDPOINTS = new String[]{"/api/websockets"};
    private static final String[] STOMP_BROKER_DESTINATIONS = new String[]{"/topic"};

    private final String host;
    private final int port;
    private final String clientUser;
    private final String clientPasscode;
    private final String systemUser;
    private final String systemPasscode;

    public WebSocketConfig(
            @Value("${zafira.rabbitmq.stomp.host}") String host,
            @Value("${zafira.rabbitmq.stomp.port}") int port,
            @Value("${zafira.rabbitmq.user}") String clientUser,
            @Value("${zafira.rabbitmq.pass}") String clientPasscode,
            @Value("${zafira.rabbitmq.user}") String systemUser,
            @Value("${zafira.rabbitmq.pass}") String systemPasscode) {
        this.host = host;
        this.port = port;
        this.clientUser = clientUser;
        this.clientPasscode = clientPasscode;
        this.systemUser = systemUser;
        this.systemPasscode = systemPasscode;
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
                             .setRelayHost(host)
                             .setRelayPort(port)
                             .setClientLogin(clientUser)
                             .setClientPasscode(clientPasscode)
                             .setSystemLogin(systemUser)
                             .setSystemPasscode(systemPasscode);
    }

}
