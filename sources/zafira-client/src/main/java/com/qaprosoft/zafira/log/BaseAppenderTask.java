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
package com.qaprosoft.zafira.log;

import com.qaprosoft.zafira.log.event.EventPublisher;

import java.util.concurrent.Callable;

/**
 * Simple Callable class that publishes messages to RabbitMQ server
 */
public abstract class BaseAppenderTask<T> implements Callable<T> {

    private EventPublisher eventPublisher;
    private String routingKey;
    private String identifier;
    private boolean zafiraConnected;
    private String correlationId;

    protected abstract String getTestId();
    protected abstract String getJsonPayload();
    protected abstract String getEventType();
    protected abstract T getEventObject();

    /**
     * Method is called by ExecutorService and publishes message on RabbitMQ
     *
     * @return loggingEvent
     */
    @Override
    public T call() throws Exception {
        publishEvent();
        return getEventObject();
    }

    private void publishEvent() {
        if (zafiraConnected) {
            String payload = getJsonPayload();
            String correlationId = getCorrelationId();
            eventPublisher.publishEvent(routingKey, correlationId, identifier, getEventType(), payload);
        }
    }

    private String buildCorrelationId(String routingKey) {
        String testId = getTestId();
        return testId != null ? routingKey + "_" + testId : routingKey;
    }

    private void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    private String getCorrelationId() {
        return correlationId;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        String correlationId = buildCorrelationId(routingKey);
        setCorrelationId(correlationId);
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setZafiraConnected(boolean zafiraConnected) {
        this.zafiraConnected = zafiraConnected;
    }

}
