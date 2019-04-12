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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.qaprosoft.zafira.dbaccess.utils.SQLTemplateAdapter;
import com.qaprosoft.zafira.services.util.FreemarkerUtil;
import com.qaprosoft.zafira.services.util.URLResolver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.WidgetMapper;
import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.dbaccess.utils.SQLAdapter;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class WidgetService {

	private static final Logger LOGGER = Logger.getLogger(WidgetService.class);

	@Autowired
	private WidgetMapper widgetMapper;

	@Autowired
	private FreemarkerUtil freemarkerUtil;

	@Autowired
	private URLResolver urlResolver;

	@Autowired
	private SettingsService settingsService;

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
	public Widget createWidget(Widget widget) throws ServiceException
	{
		widgetMapper.createWidget(widget);
		return widget;
	}

	@Transactional(readOnly = true)
	public Widget getWidgetById(long id) throws ServiceException
	{
		return widgetMapper.getWidgetById(id);
	}

	@Transactional(readOnly = true)
	public List<Widget> getAllWidgets() throws ServiceException
	{
		return widgetMapper.getAllWidgets();
	}

	@Transactional(rollbackFor = Exception.class)
	public Widget updateWidget(Widget widget) throws ServiceException
	{
		widgetMapper.updateWidget(widget);
		return widget;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteWidgetById(Long id) throws ServiceException
	{
		widgetMapper.deleteWidgetById(id);
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> executeSQL(String sql) throws ServiceException
	{
		return widgetMapper.executeSQL(new SQLAdapter(sql));
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> executeSQL(String sql, Map<String, Object> params, Map<DefaultParam, Object> additionalParams,
												boolean isSqlFreemarkerTemplate) throws ServiceException {
		if(isSqlFreemarkerTemplate) {
			sql = freemarkerUtil.getFreeMarkerTemplateContent(sql, processDefaultParameters(params, additionalParams), false);
		}
		return widgetMapper.executeSQLTemplate(new SQLTemplateAdapter(sql, params));
	}

	private Map<String, Object> processDefaultParameters(Map<String, Object> params, Map<DefaultParam, Object> additionalParams) {
		Arrays.asList(DefaultParam.values()).forEach(defaultParam -> {
			if(! params.containsKey(defaultParam.parameterName)) {
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
				case JENKINS_URL:
					result = settingsService.getSettingByName("JENKINS_URL").getValue();
					break;
				case CURRENT_USER_ID:
					result = additionalParams.get(DefaultParam.CURRENT_USER_ID);
					break;
				case CURRENT_USER_NAME:
					result = additionalParams.get(DefaultParam.CURRENT_USER_NAME);
					break;
				case HASHCODE:
					result = 0;
					break;
				case TEST_CASE_ID:
					result = 0;
					break;
			}
		} catch (ServiceException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return result;
	}
}
