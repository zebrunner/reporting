/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
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
import com.qaprosoft.zafira.models.dto.widget.WidgetTemplateParameter;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class WidgetTemplateService {

    private static final Logger LOGGER = Logger.getLogger(WidgetTemplateService.class);

    @Autowired
    private WidgetTemplateMapper widgetTemplateMapper;

    @Autowired
    private SQLUtils sqlUtils;

    private ObjectMapper mapper = new ObjectMapper();

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

    public List<WidgetTemplate> getWidgetTemplates() {
        List<WidgetTemplate> widgetTemplates = getAllWidgetTemplates().stream()
                                                                      .filter(widgetTemplate -> !widgetTemplate.getHidden())
                                                                      .peek(this::clearRedundantParamsValues)
                                                                      .collect(Collectors.toList());
        return widgetTemplates;
    }

    public WidgetTemplate prepareWidgetTemplate(WidgetTemplate widgetTemplate) throws ServiceException {
        if(widgetTemplate == null) {
            throw new ForbiddenOperationException("Unable to prepare widget template data");
        }
        executeWidgetTemplateParamsSQLQueries(widgetTemplate);
        return widgetTemplate;
    }

    public void clearRedundantParamsValues(WidgetTemplate widgetTemplate) throws ServiceException {
        if(widgetTemplate != null) {
            widgetTemplate.setParamsConfig(processParameters(widgetTemplate.getParamsConfig(), parameter -> {
                if(parameter.getValuesQuery() != null && parameter.getValues() == null) {
                    parameter.setValues(new ArrayList<>());
                }
                parameter.setValuesQuery(null);
            }));
        }
    }

    public WidgetTemplate prepareWidgetTemplateById(Long id) throws ServiceException {
        WidgetTemplate widgetTemplate = widgetTemplateMapper.getWidgetTemplateById(id);
        return prepareWidgetTemplate(widgetTemplate);
    }

    private void executeWidgetTemplateParamsSQLQueries(WidgetTemplate widgetTemplate) {
        if(widgetTemplate != null && ! StringUtils.isBlank(widgetTemplate.getParamsConfig())) {
            widgetTemplate.setParamsConfig(processParameters(widgetTemplate.getParamsConfig(), this::processParameter));
        }
    }

    private String processParameters(String paramsConfig, Consumer<WidgetTemplateParameter> parameterConsumer) {
        String result = null;
        try {
            Map<String, WidgetTemplateParameter> params = mapper.readValue(paramsConfig, new TypeReference<Map<String, WidgetTemplateParameter>>() {});
            params.forEach((name, parameter) -> parameterConsumer.accept(parameter));
            result = mapper.writeValueAsString(params);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

    private void processParameter(WidgetTemplateParameter parameter) {
        if (parameter.getValuesQuery() != null) {
            retrieveParameterValues(parameter.getValuesQuery(), parameter);
            // once query is executed it is no longer should be part of response returned to API client
            parameter.setValuesQuery(null);
        }
    }

    private void retrieveParameterValues(String query, WidgetTemplateParameter parameter) {
        List<Object> data = sqlUtils.getSingleRowResult(query);
        if (data != null) {
            if (parameter.getValues() == null) {
                parameter.setValues(new ArrayList<>());
            }
            parameter.getValues().addAll(data);
        }
    }

}
