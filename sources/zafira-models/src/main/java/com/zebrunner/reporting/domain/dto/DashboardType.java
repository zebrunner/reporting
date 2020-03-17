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
package com.zebrunner.reporting.domain.dto;

import com.zebrunner.reporting.domain.db.Attribute;
import com.zebrunner.reporting.domain.dto.widget.WidgetDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DashboardType extends AbstractType {

    private static final long serialVersionUID = -562795025453363474L;

    private String title;
    private List<WidgetDTO> widgets = new ArrayList<>();
    private boolean hidden;
    private Integer position;
    private boolean editable;
    private List<Attribute> attributes;

}