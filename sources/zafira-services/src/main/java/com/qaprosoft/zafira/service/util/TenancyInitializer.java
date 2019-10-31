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
package com.qaprosoft.zafira.service.util;

import com.google.gson.Gson;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Invitation;
import com.qaprosoft.zafira.models.push.events.EmailEventMessage;
import com.qaprosoft.zafira.models.push.events.EventMessage;
import com.qaprosoft.zafira.models.push.events.TenancyResponseEventMessage;
import com.qaprosoft.zafira.service.InvitationService;
import com.qaprosoft.zafira.service.management.TenancyService;
import com.qaprosoft.zafira.service.scm.ScmAccountService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.qaprosoft.zafira.service.util.EventPushService.Type.TENANCIES;
import static com.qaprosoft.zafira.service.util.EventPushService.Type.ZFR_CALLBACKS;

@Component
public class TenancyInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenancyInitializer.class);

    private static final String DEFAULT_USER_GROUP = "Admins";

    @Autowired
    private URLResolver urlResolver;

    @Autowired
    private TenancyService tenancyService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private ScmAccountService scmAccountService;

    @Autowired
    private EventPushService<EventMessage> eventPushService;

    private final List<TenancyDbInitial> tenancyDbInitials;
    private final List<TenancyInitial> tenancyInitials;

    public TenancyInitializer(Map<String, TenancyDbInitial> tenancyDbInitials, Map<String, TenancyInitial> tenancyInitials) {
        this.tenancyDbInitials = new ArrayList<>(tenancyDbInitials.values());
        this.tenancyInitials = new ArrayList<>(tenancyInitials.values());
    }

    /**
     * RabbitMQ listener
     * 
     * @param message - amqp message
     */
    @RabbitListener(queues = "#{tenanciesQueue.name}")
    public void initTenancy(Message message) {
        try {
            EventMessage eventMessage = new Gson().fromJson(new String(message.getBody()), EventMessage.class);
            String tenancy = eventMessage.getTenantName();
            LOGGER.info("Tenancy with name '" + tenancy + "' initialization is starting....");
            initTenancy(tenancy);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "#{zfrEventsQueue.name}")
    public void initTenancyDb(Message message) {
        TenancyResponseEventMessage result;
        try {
            boolean success = false;
            EmailEventMessage eventMessage = new Gson().fromJson(new String(message.getBody()), EmailEventMessage.class);
            String tenancy = eventMessage.getTenantName();
            result = new TenancyResponseEventMessage(tenancy);
            try {
                LOGGER.info("Tenancy with name '" + tenancy + "' DB initialization is starting....");
                tenancyDbInitials.forEach(tenancyInitial -> initTenancyDb(tenancy, tenancyInitial));

                processMessage(tenancy, () -> scmAccountService.reEncryptTokens());

                success = eventPushService.convertAndSend(TENANCIES, new EventMessage(tenancy));
                processMessage(tenancy, () -> {
                    try {
                        Invitation invitation = invitationService.createInitialInvitation(eventMessage.getEmail(), DEFAULT_USER_GROUP);
                        result.setToken(invitation.getToken());
                        result.setZafiraURL(urlResolver.buildWebURL());
                    } catch (RuntimeException e) {
                        String errorMessage = e.getMessage();
                        result.setMessage(errorMessage);

                        LOGGER.error(errorMessage, e);
                    }
                });
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                result.setMessage(errorMessage);

                LOGGER.error(errorMessage, e);
            } finally {
                result.setSuccess(success);
                eventPushService.convertAndSend(ZFR_CALLBACKS, result, "Event-Type", "ZFR_INIT_TENANCY");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void initTenancy(String tenancy) {
        tenancyInitials.forEach(tenancyInitial -> initTenancy(tenancy, tenancyInitial));
    }

    /**
     * Trigger to execute some task on tenancy creation
     * 
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
        if (!StringUtils.isBlank(tenancy) && tenancyService.getTenancyByName(tenancy) != null) {
            TenancyContext.setTenantName(tenancy);
            runnable.run();
            TenancyContext.setTenantName(null);
        }
    }
}
