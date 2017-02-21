package com.qaprosoft.zafira.services.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.WidgetMapper;
import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.dbaccess.utils.SQLAdapter;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class WidgetService
{
	@Autowired
	private WidgetMapper widgetMapper;

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
}