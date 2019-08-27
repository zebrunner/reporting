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
package com.qaprosoft.zafira.models.dto.integration;

import com.qaprosoft.zafira.models.dto.AbstractType;

import java.util.List;

public class IntegrationGroupDTO extends AbstractType {

    private String name;
    private String iconUrl;
    private boolean multipleAllowed;
    private List<IntegrationTypeDTO> integrationTypeDTOS;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isMultipleAllowed() {
        return multipleAllowed;
    }

    public void setMultipleAllowed(boolean multipleAllowed) {
        this.multipleAllowed = multipleAllowed;
    }

    public List<IntegrationTypeDTO> getIntegrationTypeDTOS() {
        return integrationTypeDTOS;
    }

    public void setIntegrationTypeDTOS(List<IntegrationTypeDTO> integrationTypeDTOS) {
        this.integrationTypeDTOS = integrationTypeDTOS;
    }
}
