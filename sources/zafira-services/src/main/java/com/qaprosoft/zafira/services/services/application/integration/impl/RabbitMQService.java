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

import java.util.List;

import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.Connection;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.context.RabbitMQContext;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQService extends AbstractIntegration<RabbitMQContext> {

    private static final Logger LOGGER = Logger.getLogger(RabbitMQService.class);

    private final SettingsService settingsService;
    private final CryptoService cryptoService;
    private final Queue settingsQueue;

    public RabbitMQService(SettingsService settingsService, CryptoService cryptoService, Queue settingsQueue) {
        super(RABBITMQ);
        this.settingsService = settingsService;
        this.cryptoService = cryptoService;
        this.settingsQueue = settingsQueue;
    }

    @Override
    public void init() {
        String host = null;
        String port = null;
        String username = null;
        String password = null;
        boolean enabled = false;

        try {
            List<Setting> rabbitmqSettings = settingsService.getSettingsByTool(RABBITMQ);
            for (Setting setting : rabbitmqSettings) {
                if (setting.isEncrypted()) {
                    setting.setValue(cryptoService.decrypt(setting.getValue()));
                }
                switch (Setting.SettingType.valueOf(setting.getName())) {
                case RABBITMQ_HOST:
                    host = setting.getValue();
                    break;
                case RABBITMQ_PORT:
                    port = setting.getValue();
                    break;
                case RABBITMQ_USER:
                    username = setting.getValue();
                    break;
                case RABBITMQ_PASSWORD:
                    password = setting.getValue();
                    break;
                case RABBITMQ_ENABLED:
                    enabled = Boolean.valueOf(setting.getValue());
                    break;
                default:
                    break;
                }
            }
            init(host, port, username, password, enabled);
        } catch (Exception e) {
            LOGGER.error("Setting does not exist", e);
        }
    }

    public void init(String host, String port, String username, String password, boolean enabled) {
        try {
            if (!StringUtils.isEmpty(host) && !StringUtils.isEmpty(port) && !StringUtils.isEmpty(username)
                    && !StringUtils.isEmpty(password)) {
                putContext(new RabbitMQContext(host, port, username, password, enabled));
            }
        } catch (Exception e) {
            LOGGER.error("Unable to initialize RabbitMQ integration: " + e.getMessage());
        }
    }

    public String getSettingQueueName() {
        return this.settingsQueue.getName();
    }

    public boolean isSettingQueueConsumer(String settingQueueName) {
        return settingQueueName.equals(getSettingQueueName());
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
