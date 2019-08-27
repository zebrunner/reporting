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
package com.qaprosoft.zafira.models.db.integration;

import com.qaprosoft.zafira.models.db.AbstractEntity;

import java.util.List;
import java.util.Optional;

public class Integration extends AbstractEntity {

    private String name;
    private String backReferenceId;
    private boolean isDefault;
    private boolean enabled;
    private List<IntegrationSetting> integrationSettings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackReferenceId() {
        return backReferenceId;
    }

    public void setBackReferenceId(String backReferenceId) {
        this.backReferenceId = backReferenceId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<IntegrationSetting> getIntegrationSettings() {
        return integrationSettings;
    }

    public void setIntegrationSettings(List<IntegrationSetting> integrationSettings) {
        this.integrationSettings = integrationSettings;
    }

    public Optional<IntegrationSetting> getAttribute(String attributeName) {
        return this.getIntegrationSettings().stream()
                   .filter(is -> is.getIntegrationParam().getName().equals(attributeName))
                   .findAny();
    }

    public Optional<String> getAttributeValue(String attributeName) {
        IntegrationSetting integrationSetting = getAttribute(attributeName)
                .orElse(null);
        String value = integrationSetting == null ? null : integrationSetting.getValue();
        return Optional.ofNullable(value);
    }

    public Optional<byte[]> getAttributeBinaryData(String attributeName) {
        IntegrationSetting integrationSetting = getAttribute(attributeName)
                .orElse(null);
        byte[] binaryData = integrationSetting == null ? null : integrationSetting.getBinaryData();
        return Optional.ofNullable(binaryData);
    }

}
