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
package com.qaprosoft.zafira.services.services.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.management.WidgetTemplateMapper;
import com.qaprosoft.zafira.models.db.WidgetTemplate;
import com.qaprosoft.zafira.services.util.SQLUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WidgetTemplateService {

    private static final Logger LOGGER = Logger.getLogger(WidgetTemplateService.class);

    @Autowired
    private WidgetTemplateMapper widgetTemplateMapper;

    @Autowired
    private SQLUtils sqlUtils;

    @Transactional(readOnly = true)
    public WidgetTemplate getWidgetTemplateById(Long id) {
        return widgetTemplateMapper.getWidgetTemplateById(id);
    }

    @Transactional(readOnly = true)
    public WidgetTemplate getWidgetTemplateByName(String name) {
        return widgetTemplateMapper.getWidgetTemplateByName(name);
    }

    @Transactional(readOnly = true)
    public List<WidgetTemplate> getAllWidgetTemplates() {
        return widgetTemplateMapper.getAllWidgetTemplates();
    }

    public List<WidgetTemplate> getProcessedWidgetTemplates() {
        List<WidgetTemplate> widgetTemplates = getAllWidgetTemplates();
        widgetTemplates.forEach(this::executeWidgetTemplateParamsSQLQueries);
        return widgetTemplates;
    }

    public void executeWidgetTemplateParamsSQLQueries(WidgetTemplate widgetTemplate) {
        if(widgetTemplate != null && ! StringUtils.isBlank(widgetTemplate.getParamsConfig())) {
            widgetTemplate.setParamsConfig(executeWidgetTemplateSQLQueries(widgetTemplate.getParamsConfig()));
        }
    }

    @SuppressWarnings("unchecked")
    private String executeWidgetTemplateSQLQueries(String paramsConfig) {
        ObjectMapper mapper = new ObjectMapper();
        String result = null;
        try {
            Map<String, Map<String, Object>> configs = mapper.readValue(paramsConfig, new TypeReference<Map<String, Map<String, Object>>>() {});
            configs.forEach((configName, configValue) -> configValue.forEach((configParamName, configParamValue) -> {
                if (configParamName.equals("values") && configParamValue instanceof List) {
                    List<Object> collector = new ArrayList<>();
                    ((List) configParamValue).forEach(value -> {
                        if (value instanceof String && value.toString().trim().toLowerCase().startsWith("select")) {
                            collector.addAll(sqlUtils.getSingleRowResult(value.toString()));
                        } else {
                            collector.add(value);
                        }
                    });
                    ((List) configParamValue).clear();
                    ((ArrayList) configParamValue).addAll(collector);
                }
            }));
            result = mapper.writeValueAsString(configs);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }
}
