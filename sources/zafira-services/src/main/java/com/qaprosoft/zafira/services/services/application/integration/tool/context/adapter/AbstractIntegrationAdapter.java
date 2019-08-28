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
package com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter;

import com.qaprosoft.zafira.models.db.integration.Integration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIntegrationAdapter implements IntegrationAdapter {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractIntegrationAdapter.class);

    private static final String ERR_MSG_PARAMETER_NOT_FOUND = "Parameter with name '%s' not found for type '%s'";

    private final Long integrationId;

    public AbstractIntegrationAdapter(Integration integration) {
        this.integrationId = integration.getId();
    }

    @Override
    public Long getIntegrationId() {
        return integrationId;
    }

    // todo move this to Integration.java: Optional<String> getSettingValue(String name)
    public String getAttributeValue(AdapterParam adapterParam) {
        return null;
//        return integration.getAttributeValue(adapterParam.getName())
//                          .orElseThrow(() -> new IntegrationException(String.format(ERR_MSG_PARAMETER_NOT_FOUND, adapterParam.getName(), type)));
    }

    // todo move this to Integration.java: getSettingValueAsBinaryData(String name)
    public byte[] getAttributeBinaryData(AdapterParam adapterParam) {
        return null;
//        return integration.getAttributeBinaryData(adapterParam.getName())
//                          .orElseThrow(() -> new IntegrationException(String.format(ERR_MSG_PARAMETER_NOT_FOUND, adapterParam.getName(), type)));

    }

}
