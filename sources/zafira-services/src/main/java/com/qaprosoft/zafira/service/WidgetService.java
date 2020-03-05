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
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.WidgetMapper;
import com.qaprosoft.zafira.dbaccess.utils.SQLTemplateAdapter;
import com.qaprosoft.zafira.models.db.Attribute;
import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.models.db.WidgetTemplate;
import com.qaprosoft.zafira.service.exception.ProcessingException;
import com.qaprosoft.zafira.service.util.FreemarkerUtil;
import com.qaprosoft.zafira.service.util.SQLExecutor;
import com.qaprosoft.zafira.service.util.URLResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qaprosoft.zafira.service.exception.ProcessingException.ProcessingErrorDetail.WIDGET_QUERY_EXECUTION_ERROR;

@Service
public class WidgetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetService.class);
    private static final String ERR_MSG_INVALID_CHART_QUERY = "Invalid chart query";

    private final WidgetMapper widgetMapper;
    private final FreemarkerUtil freemarkerUtil;
    private final URLResolver urlResolver;
    private final WidgetTemplateService widgetTemplateService;
    private final SQLExecutor sqlExecutor;

    public WidgetService(WidgetMapper widgetMapper, FreemarkerUtil freemarkerUtil, URLResolver urlResolver, WidgetTemplateService widgetTemplateService, SQLExecutor sqlExecutor) {
        this.widgetMapper = widgetMapper;
        this.freemarkerUtil = freemarkerUtil;
        this.urlResolver = urlResolver;
        this.widgetTemplateService = widgetTemplateService;
        this.sqlExecutor = sqlExecutor;
    }

    public enum DefaultParam {
        SERVICE_URL("serviceUrl"),
        JENKINS_URL("jenkinsUrl"),
        CURRENT_USER_ID("currentUserId"),
        CURRENT_USER_NAME("currentUserName"),
        HASHCODE("hashcode"),
        TEST_CASE_ID("testCaseId");

        private final String parameterName;

        DefaultParam(String parameterName) {
            this.parameterName = parameterName;
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public Widget createWidget(Widget widget) {
        if (widget.getWidgetTemplate() != null) {
            WidgetTemplate widgetTemplate = prepareWidgetTemplate(widget);
            widget.setType(widgetTemplate.getType().name());
        }
        widgetMapper.createWidget(widget);
        return widget;
    }

    @Transactional(readOnly = true)
    public Widget getWidgetById(long id) {
        return widgetMapper.getWidgetById(id);
    }

    @Transactional(readOnly = true)
    public List<Widget> getAllWidgets() {
        return widgetMapper.getAllWidgets();
    }

    @Transactional(rollbackFor = Exception.class)
    public Widget updateWidget(Widget widget) {
        if (widget.getWidgetTemplate() != null) {
            prepareWidgetTemplate(widget);
        }
        widgetMapper.updateWidget(widget);
        return widget;
    }

    private WidgetTemplate prepareWidgetTemplate(Widget widget) {
        long templateId = widget.getWidgetTemplate().getId();
        WidgetTemplate widgetTemplate = widgetTemplateService.getNotNullWidgetTemplateById(templateId);
        widgetTemplateService.clearRedundantParamsValues(widgetTemplate);
        widget.setWidgetTemplate(widgetTemplate);
        return widgetTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteWidgetById(Long id) {
        widgetMapper.deleteWidgetById(id);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getQueryResults(Map<String, Object> params, Long templateId, Long userId, String userName) {
        WidgetTemplate widgetTemplate = widgetTemplateService.getNotNullWidgetTemplateById(templateId);
        List<Map<String, Object>> resultList;
        try {
            Map<WidgetService.DefaultParam, Object> additionalParams = new HashMap<>();
            additionalParams.put(WidgetService.DefaultParam.CURRENT_USER_NAME, userName);
            additionalParams.put(WidgetService.DefaultParam.CURRENT_USER_ID, userId);
            resultList = executeSQL(widgetTemplate.getSql(), params, additionalParams, true);
        } catch (Exception e) {
            throw new ProcessingException(WIDGET_QUERY_EXECUTION_ERROR, ERR_MSG_INVALID_CHART_QUERY);
        }
        return resultList;
    }

    /**
     * Used for old widget versions and table widgets.
     * Someday we'll remove echarts library completely and refactor this method.
     */

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getQueryResultObsolete(
            List<String> projects,
            String currentUserId,
            String dashboardName,
            String query,
            List<Attribute> attributes,
            Long userId,
            String userName
    ) {
        List<Map<String, Object>> resultList;
        try {
            query = applyAttributes(attributes, query);
            query = replacePlaceholders(projects, currentUserId, dashboardName, query, userId, userName);
            resultList = executeSQL(query);
        } catch (Exception e) {
            throw new ProcessingException(WIDGET_QUERY_EXECUTION_ERROR, ERR_MSG_INVALID_CHART_QUERY);
        }
        return resultList;
    }

    private String applyAttributes(List<Attribute> attributes, String query) {
        if (attributes != null) {
            for (Attribute attribute : attributes) {
                query = query.replaceAll("#\\{" + attribute.getKey() + "\\}", attribute.getValue());
            }
        }
        return query;
    }

    private String replacePlaceholders(List<String> projects, String currentUserId, String dashboardName, String query, Long userId, String userName) {
        query = query
                .replaceAll("#\\{project}", concatProjectNames(projects))
                .replaceAll("#\\{dashboardName}", !StringUtils.isEmpty(dashboardName) ? dashboardName : "")
                .replaceAll("#\\{currentUserId}", !StringUtils.isEmpty(currentUserId) ? currentUserId : String.valueOf(userId))
                .replaceAll("#\\{currentUserName}", String.valueOf(userName))
                .replaceAll("#\\{zafiraURL}", urlResolver.buildWebURL())
                .replaceAll("#\\{hashcode}", "0")
                .replaceAll("#\\{testCaseId}", "0");
        return query;
    }

    private String concatProjectNames(List<String> projects) {
        return !CollectionUtils.isEmpty(projects) ? String.join(",", projects) : "%";
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> executeSQL(String sql) {
        return sqlExecutor.getMultiRowResult(sql);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> executeSQL(String sql, Map<String, Object> params, Map<DefaultParam, Object> additionalParams,
                                                boolean isSqlFreemarkerTemplate) {
        if (isSqlFreemarkerTemplate) {
            sql = freemarkerUtil.getFreeMarkerTemplateContent(sql, processDefaultParameters(params, additionalParams), false);
        }
        return widgetMapper.executeSQLTemplate(new SQLTemplateAdapter(sql, params));
    }

    private Map<String, Object> processDefaultParameters(Map<String, Object> params, Map<DefaultParam, Object> additionalParams) {
        Arrays.asList(DefaultParam.values()).forEach(defaultParam -> {
            if (!params.containsKey(defaultParam.parameterName)) {
                params.put(defaultParam.parameterName, getDefaultParamValue(defaultParam, additionalParams));
            }
        });
        return params;
    }

    private Object getDefaultParamValue(DefaultParam param, Map<DefaultParam, Object> additionalParams) {
        Object result = null;
        try {
            switch (param) {
                case SERVICE_URL:
                    result = urlResolver.getServiceURL();
                    break;
                case CURRENT_USER_ID:
                case CURRENT_USER_NAME:
                    result = additionalParams.get(param);
                    break;
                case HASHCODE:
                case TEST_CASE_ID:
                    result = 0;
                    break;
            }
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }
}
