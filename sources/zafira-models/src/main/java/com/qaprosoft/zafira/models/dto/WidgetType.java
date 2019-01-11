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

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

public class WidgetType extends AbstractType {

    private static final long serialVersionUID = -8163778207543974125L;

    @NotEmpty(message = "{error.title.required}")
    private String title;

    private String description;

    private String paramsConfig;

    private String legendConfig;

    @Valid
    private WidgetTemplateType widgetTemplate;

    private boolean refreshable;

    private String type;
    private Integer size;
    private Integer position;
    private String location;
    private String sql;
    private String model;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParamsConfig() {
        return paramsConfig;
    }

    public void setParamsConfig(String paramsConfig) {
        this.paramsConfig = paramsConfig;
    }

    public String getLegendConfig() {
        return legendConfig;
    }

    public void setLegendConfig(String legendConfig) {
        this.legendConfig = legendConfig;
    }

    public WidgetTemplateType getWidgetTemplate() {
        return widgetTemplate;
    }

    public void setWidgetTemplate(WidgetTemplateType widgetTemplate) {
        this.widgetTemplate = widgetTemplate;
    }

    public boolean isRefreshable() {
        return refreshable;
    }

    public void setRefreshable(boolean refreshable) {
        this.refreshable = refreshable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
