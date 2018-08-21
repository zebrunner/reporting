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
package com.qaprosoft.zafira.services.services.jmx;

import static com.qaprosoft.zafira.models.db.Setting.Tool.RABBITMQ;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.services.services.jmx.models.RabbitMQType;

@ManagedResource(objectName = "bean:name=rabbitMQService", description = "RabbitMQ init Managed Bean", currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200, persistLocation = "foo", persistName = "bar")
public class RabbitMQService implements IJMXService<RabbitMQType> {
    private static final Logger LOGGER = Logger.getLogger(RabbitMQService.class);

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CryptoService cryptoService;

    @Override
    @PostConstruct
    public void init() {
        String host = null;
        String port = null;
        String username = null;
        String password = null;

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
                default:
                    break;
                }
            }
            init(host, port, username, password);
        } catch (Exception e) {
            LOGGER.error("Setting does not exist", e);
        }
    }

    @ManagedOperation(description = "Change RabbitMQ initialization")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "host", description = "RabbitMQ host"),
            @ManagedOperationParameter(name = "port", description = "RabbitMQ port"),
            @ManagedOperationParameter(name = "username", description = "RabbitMQ username"),
            @ManagedOperationParameter(name = "password", description = "RabbitMQ password") })
    public void init(String host, String port, String username, String password) {
        try {
            if (!StringUtils.isEmpty(host) && !StringUtils.isEmpty(port) && !StringUtils.isEmpty(username)
                    && !StringUtils.isEmpty(password)) {
                putType(RABBITMQ, new RabbitMQType(host, port, username, password));
            }
        } catch (Exception e) {
            LOGGER.error("Unable to initialize RabbitMQ integration: " + e.getMessage());
        }
    }

    @Override
    public boolean isConnected() {
        return getConnection() != null && getConnection().isOpen();
    }

    @ManagedAttribute(description = "Get rabbitMQ connection")
    public Connection getConnection() {
        return getType(RABBITMQ) != null ? getType(RABBITMQ).getConnection() : null;
    }
}