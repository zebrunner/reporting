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
 ******************************************************************************/
package com.qaprosoft.zafira.service.integration.core;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.models.push.events.ReinitEventMessage;
import com.qaprosoft.zafira.service.CryptoService;
import com.qaprosoft.zafira.service.integration.IntegrationService;
import com.qaprosoft.zafira.service.integration.IntegrationSettingService;
import com.qaprosoft.zafira.service.integration.tool.proxy.IntegrationAdapterProxy;
import com.qaprosoft.zafira.service.management.TenancyService;
import com.qaprosoft.zafira.service.util.EventPushService;
import com.qaprosoft.zafira.service.util.TenancyDbInitial;
import com.qaprosoft.zafira.service.util.TenancyInitial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
@DependsOn("databaseStateManager")
public class IntegrationTenancyStorage implements TenancyInitial, TenancyDbInitial {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTenancyStorage.class);

    private final TenancyService tenancyService;
    private final IntegrationService integrationService;
    private final IntegrationInitializer integrationInitializer;
    private final IntegrationSettingService integrationSettingService;
    private final EventPushService eventPushService;
    private final Map<String, IntegrationAdapterProxy> integrationProxies;
    private final CryptoService cryptoService;

    public IntegrationTenancyStorage(
            TenancyService tenancyService,
            IntegrationService integrationService,
            IntegrationInitializer integrationInitializer,
            IntegrationSettingService integrationSettingService,
            EventPushService eventPushService,
            Map<String, IntegrationAdapterProxy> integrationProxies, CryptoService cryptoService) {
        this.tenancyService = tenancyService;
        this.integrationService = integrationService;
        this.integrationInitializer = integrationInitializer;
        this.integrationSettingService = integrationSettingService;
        this.eventPushService = eventPushService;
        this.integrationProxies = integrationProxies;
        this.cryptoService = cryptoService;
    }

    @PostConstruct
    public void post() {
        tenancyService.iterateItems(() -> {
            initDb();
            init();
        });
    }

    @Override
    public void init() {
        integrationProxies.forEach((name, proxy) -> proxy.init());
    }

    @Override
    public void initDb() {
        try {
            cryptoService.init();
            List<Integration> integrations = integrationService.retrieveAll();
            integrations.forEach(integration -> integration.getSettings().forEach(integrationSetting -> {
                if (!StringUtils.isEmpty(integrationSetting.getValue()) && integrationSetting.getParam().isNeedEncryption() && !integrationSetting.isEncrypted()) {
                    integrationSetting.setValue(cryptoService.encrypt(integrationSetting.getValue()));
                    integrationSetting.setEncrypted(true);
                    integrationSettingService.update(integrationSetting);
                }
            }));
        } catch (Exception e) {
            LOGGER.error("Unable to encrypt value: " + e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "#{settingsQueue.name}")
    public void process(Message message) {
        try {
            ReinitEventMessage event = new Gson().fromJson(new String(message.getBody()), ReinitEventMessage.class);

            long integrationId = event.getIntegrationId();
            String tenantName = event.getTenantName();
            try {
                if (!eventPushService.isSettingQueueConsumer(message)) {
                    TenancyContext.setTenantName(tenantName);

                    Integration integration = integrationService.retrieveById(integrationId);
                    integrationInitializer.initIntegration(integration, tenantName);

                    TenancyContext.setTenantName(null);
                }
            } catch (Exception e) {
                LOGGER.error(String.format("Unable to initialize adapter for integration with id %d. ", integrationId) + e.getMessage(), e);
            }
        } catch (JsonIOException | JsonSyntaxException e) {
            LOGGER.error("Unable to map even message to ReinitEventMessage type. " + e.getMessage(), e);
        }
    }

}
