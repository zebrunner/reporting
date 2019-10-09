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
package com.qaprosoft.zafira.service;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Configuration
public class ExchangeConfig {

    private final String exchangeName;
    private final RabbitTemplate rabbitTemplate;

    public ExchangeConfig(
            @Value("${spring.rabbitmq.template.exchange}") String exchangeName,
            RabbitTemplate rabbitTemplate
    ) {
        this.exchangeName = exchangeName;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void init() {
        this.rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }

    @Bean
    public DirectExchange eventsTopicExchange() {
        return new DirectExchange(exchangeName, false, true);
    }

    /**
     * Queue with autogenerated name to get rid of duplicate names on different app instances
     * @return setting queue with prefix 'settingsQueue'
     */
    @Bean
    public Queue settingsQueue() {
        return new Queue(generateQueueName("settingsQueue"), false, false, true);
    }

    /**
     * Queue with autogenerated name to get rid of duplicate names on different app instances
     * @return tenancy queue with prefix 'tenanciesQueue'
     */
    @Bean
    public Queue tenanciesQueue() {
        return new Queue(generateQueueName("tenanciesQueue"), false, false, true);
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

    private String generateQueueName(String prefix) {
        return String.format("%s-%s", prefix, UUID.randomUUID().toString());
    }

}