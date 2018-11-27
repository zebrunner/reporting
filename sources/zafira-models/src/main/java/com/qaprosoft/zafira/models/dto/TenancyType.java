/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.models.dto;

import java.io.Serializable;
import java.util.Arrays;

import javax.validation.constraints.AssertTrue;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qaprosoft.zafira.models.db.Tenancy;

public class TenancyType implements Serializable {

    private static final long serialVersionUID = 8230787643243488944L;

    @NotEmpty(message = "{error.name.required}")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @AssertTrue(message = "{error.name.invalid}")
    @JsonIgnore
    public boolean isNameConfirmationValid() {
        return ! Arrays.asList(Tenancy.getDefaultNames()).contains(name.toLowerCase());
    }
}
