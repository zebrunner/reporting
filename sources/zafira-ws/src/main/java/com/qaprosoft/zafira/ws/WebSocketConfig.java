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

    private static final String[] STOMP_ENDPOINTS = new String[] {
            "/api/websockets"
    };

    private static final String[] STOMP_BROKER_DESTINATIONS = new String[] {
            "/topic"
    };

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
