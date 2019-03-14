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
import com.qaprosoft.zafira.services.services.management.TenancyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TenancyInitializer {

    @Autowired
    private TenancyService tenancyService;

    private List<TenancyInitial> tenancyInitials;

    public TenancyInitializer(List<TenancyInitial> tenancyInitials) {
        this.tenancyInitials = tenancyInitials;
    }

    /**
     * RabbitMQ listener
     * @param message - amqp message
     */
    @RabbitListener(queues = "#{tenanciesQueue.name}")
    public void initTenancy(Message message) {
        tenancyInitials.forEach(tenancyInitial -> initTenancy(message, tenancyInitial));
    }

    /**
     * Trigger to execute some task on tenancy creation
     * @param message - amqp message
     * @param tenancyInitial - task to execute
     */
    public void initTenancy(Message message, TenancyInitial tenancyInitial) {
        String tenancy = new Gson().fromJson(new String(message.getBody()), String.class);
        if (! StringUtils.isBlank(tenancy) && tenancyService.getTenancyByName(tenancy) != null) {
            TenancyContext.setTenantName(tenancy);
            tenancyInitial.init();
            TenancyContext.setTenantName(null);
        }
    }
}
