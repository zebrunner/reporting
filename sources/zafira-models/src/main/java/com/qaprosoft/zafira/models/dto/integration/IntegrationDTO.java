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
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class IntegrationDTO extends AbstractType {

    @NotNull(message = "Name required")
    @Size(min = 2, max = 50, message = "Name must to be between 2 and 50 symbols")
    private String name;
    private String backReferenceId;
    private boolean isDefault;
    private boolean enabled;
    private boolean connected;

    @Valid
    @NotEmpty(message = "Integration settings required")
    private List<IntegrationSettingDTO> settings;

    private IntegrationTypeDTO type;
}
