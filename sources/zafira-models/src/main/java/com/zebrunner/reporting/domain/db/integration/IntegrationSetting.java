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
package com.zebrunner.reporting.domain.db.integration;

import com.zebrunner.reporting.domain.db.AbstractEntity;

public class IntegrationSetting extends AbstractEntity {

    private String value;
    private byte[] binaryData;
    private boolean encrypted;
    private IntegrationParam integrationParam;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public IntegrationParam getIntegrationParam() {
        return integrationParam;
    }

    public void setIntegrationParam(IntegrationParam integrationParam) {
        this.integrationParam = integrationParam;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (obj instanceof IntegrationSetting) {
            IntegrationSetting integrationSetting = (IntegrationSetting) obj;
            if (integrationSetting.getId() != null && getId() != null) {
                equals = hashCode() == integrationSetting.hashCode();
            } else if (integrationParam != null && integrationSetting.getIntegrationParam() != null) {
                equals = integrationParam.equals(integrationSetting.getIntegrationParam());
            }
        }
        return equals;
    }

}
