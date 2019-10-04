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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class IntegrationSettingDTO extends AbstractType {

    @NotNull(message = "Value required")
    private String value;
    private byte[] binaryData;
    private boolean encrypted;

    @Valid
    @NotNull(message = "Integration params required")
    private IntegrationParamDTO param;

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

    public IntegrationParamDTO getParam() {
        return param;
    }

    public void setParam(IntegrationParamDTO param) {
        this.param = param;
    }
}