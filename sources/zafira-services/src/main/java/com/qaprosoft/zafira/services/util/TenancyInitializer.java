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
package com.qaprosoft.zafira.services.util;

import com.google.gson.Gson;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.push.events.EventMessage;
import com.qaprosoft.zafira.models.push.events.TenancyResponseEventMessage;
import com.qaprosoft.zafira.services.services.management.TenancyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.qaprosoft.zafira.services.util.EventPushService.Type.TENANCIES;
import static com.qaprosoft.zafira.services.util.EventPushService.Type.ZFR_CALLBACKS;

public class TenancyInitializer {

    private static final Logger LOGGER = Logger.getLogger(TenancyInitializer.class);

    @Autowired
    private TenancyService tenancyService;

    @Autowired
    private EventPushService<EventMessage> eventPushService;

    private final List<TenancyDbInitial> tenancyDbInitials;
    private final List<TenancyInitial> tenancyInitials;

    public TenancyInitializer(List<TenancyDbInitial> tenancyDbInitials, List<TenancyInitial> tenancyInitials) {
        this.tenancyDbInitials = tenancyDbInitials;
        this.tenancyInitials = tenancyInitials;
    }

    /**
     * RabbitMQ listener
     * @param message - amqp message
     */
    @RabbitListener(queues = "#{tenanciesQueue.name}")
    public void initTenancy(Message message) {
        try {
            EventMessage eventMessage = new Gson().fromJson(new String(message.getBody()), EventMessage.class);
            String tenancy = eventMessage.getTenancy();
            LOGGER.info("Tenancy with name '" + tenancy + "' initialization is starting....");
            tenancyInitials.forEach(tenancyInitial -> initTenancy(tenancy, tenancyInitial));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "#{zfrEventsQueue.name}")
    public void initTenancyDb(Message message) {
        try {
            boolean result = false;
            EventMessage eventMessage = new Gson().fromJson(new String(message.getBody()), EventMessage.class);
            String tenancy = eventMessage.getTenancy();
            try {
                LOGGER.info("Tenancy with name '" + tenancy + "' DB initialization is starting....");
                tenancyDbInitials.forEach(tenancyInitial -> initTenancyDb(tenancy, tenancyInitial));
                result = eventPushService.convertAndSend(TENANCIES, new EventMessage(tenancy));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                eventPushService.convertAndSend(ZFR_CALLBACKS, new TenancyResponseEventMessage(tenancy, result), "Event-Type", "ZFR_INIT_TENANCY");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Trigger to execute some task on tenancy creation
     * @param tenancy - to initialize
     * @param tenancyInitial - task to execute
     */
    private void initTenancy(String tenancy, TenancyInitial tenancyInitial) {
        processMessage(tenancy, tenancyInitial::init);
    }

    private void initTenancyDb(String tenancy, TenancyDbInitial tenancyInitial) {
        processMessage(tenancy, tenancyInitial::initDb);
    }

    private void processMessage(String tenancy, Runnable runnable) {
        if (! StringUtils.isBlank(tenancy) && tenancyService.getTenancyByName(tenancy) != null) {
            TenancyContext.setTenantName(tenancy);
            runnable.run();
            TenancyContext.setTenantName(null);
        }
    }
}
