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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qaprosoft.zafira.models.dto.AbstractType;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildParameterType extends AbstractType
{
    private static final long serialVersionUID = 3647801256222745555L;

    public enum BuildParameterClass {
        STRING, BOOLEAN, HIDDEN
    }

    public BuildParameterType() {
    }

    public BuildParameterType(BuildParameterClass parameterClass, String name, String value) {
        this.parameterClass = parameterClass;
        this.name = name;
        this.value = value;
    }

    @NotNull
    private BuildParameterClass parameterClass;
    @NotNull
    private String name;
    @NotNull
    private String value;

    public BuildParameterClass getParameterClass() {
        return parameterClass;
    }

    public void setParameterClass(BuildParameterClass parameterClass) {
        this.parameterClass = parameterClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
