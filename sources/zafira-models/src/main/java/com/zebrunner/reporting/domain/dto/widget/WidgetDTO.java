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
package com.zebrunner.reporting.domain.dto.widget;

import com.zebrunner.reporting.domain.dto.AbstractType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

@Getter
@Setter
public class WidgetDTO extends AbstractType {

    private static final long serialVersionUID = -8163778207543974125L;

    @NotEmpty(message = "{error.title.required}")
    private String title;

    private String description;
    private String paramsConfig;
    private String legendConfig;

    @Valid
    private WidgetTemplateDTO widgetTemplate;

    private boolean refreshable;
    private String type;
    private Integer size;
    private Integer position;
    private String location;
    private String sql;
    private String model;

}
