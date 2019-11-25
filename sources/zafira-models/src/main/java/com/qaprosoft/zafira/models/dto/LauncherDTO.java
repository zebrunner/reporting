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
package com.qaprosoft.zafira.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class LauncherDTO extends AbstractType {

    private static final long serialVersionUID = 7778329756348322538L;

    @NotEmpty(message = "{error.name.required}")
    private String name;

    @NotEmpty(message = "{error.model.required}")
    private String model;

    private String type;

    @NotNull
    @Valid
    private ScmAccountType scmAccountType;

    @Valid
    private List<LauncherPresetDTO> presets;

    private JobDTO job;
    private boolean autoScan;


}
