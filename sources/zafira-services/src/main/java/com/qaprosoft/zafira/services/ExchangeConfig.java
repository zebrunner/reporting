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
package com.qaprosoft.zafira.services;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class ExchangeConfig {

    @Bean
    public ConnectionFactory rabbitConnectionFactory(
            @Value("${zafira.rabbitmq.stomp.host}") String hostname,
            @Value("${zafira.rabbitmq.port}") int port,
            @Value("${zafira.rabbitmq.user}") String username,
            @Value("${zafira.rabbitmq.pass}") String password,
            @Value("${zafira.rabbitmq.vhost}") String virtualHost
    ) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(hostname, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        return connectionFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory rabbitConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitConnectionFactory);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(25);
        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory rabbitConnectionFactory) {
        return new RabbitAdmin(rabbitConnectionFactory);
    }

    @Bean
    public Queue settingsQueue() {
        return new Queue("settingsQueue", false, false, true);
    }

    @Bean
    public Queue tenanciesQueue() {
        return new Queue("tenanciesQueue", false, false, true);
    }

    @Bean
    public Queue zfrEventsQueue() {
        return new Queue("zfrEventsQueue", false, false, true);
    }

    @Bean
    public Queue zfrCallbacksQueue() {
        return new Queue("zfrCallbacksQueue", false, false, true);
    }

    @Bean
    public DirectExchange eventsTopicExchange() {
        return new DirectExchange("events", false, true);
    }

    @Bean
    public Binding settingsBinding(DirectExchange exchange, Queue settingsQueue) {
        return BindingBuilder.bind(settingsQueue).to(exchange).with("settings");
    }

    @Bean
    public Binding tenanciesBinding(DirectExchange exchange, Queue tenanciesQueue) {
        return BindingBuilder.bind(tenanciesQueue).to(exchange).with("tenancies");
    }

    @Bean
    public Binding zfrEventsBinding(DirectExchange exchange, Queue zfrEventsQueue) {
        return BindingBuilder.bind(zfrEventsQueue).to(exchange).with("zfr_events");
    }

    @Bean
    public Binding zfrCallbacksBinding(DirectExchange exchange, Queue zfrCallbacksQueue) {
        return BindingBuilder.bind(zfrCallbacksQueue).to(exchange).with("zfr_callbacks");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate eventsTemplate(ConnectionFactory rabbitConnectionFactory) {
        RabbitTemplate template = new RabbitTemplate(rabbitConnectionFactory);
        template.setExchange("eventsTopicExchange");
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

}
