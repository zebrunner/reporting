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

import java.util.List;
import java.util.Map;

import com.qaprosoft.zafira.dbaccess.utils.SQLTemplateAdapter;
import com.qaprosoft.zafira.services.util.FreemarkerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.WidgetMapper;
import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.dbaccess.utils.SQLAdapter;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class WidgetService
{
	@Autowired
	private WidgetMapper widgetMapper;

	@Autowired
	private FreemarkerUtil freemarkerUtil;

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
	public List<Map<String, Object>> executeSQL(String sql, Map<String, Object> params, boolean isSqlFreemarkerTemplate) throws ServiceException
	{
		if(isSqlFreemarkerTemplate) {
			sql = freemarkerUtil.getFreeMarkerTemplateContent(sql, params, false);
		}
		return widgetMapper.executeSQLTemplate(new SQLTemplateAdapter(sql, params));
	}
}
