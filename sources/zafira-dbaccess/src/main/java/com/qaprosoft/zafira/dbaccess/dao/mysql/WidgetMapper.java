package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.models.db.Widget;
import com.qaprosoft.zafira.dbaccess.utils.SQLAdapter;

import java.util.List;
import java.util.Map;

public interface WidgetMapper
{
	List<Map<String, Object>> executeSQL(SQLAdapter sql);

	void createWidget(Widget widget);

	Widget getWidgetById(Long id);

	List<Widget> getAllWidgets();

	void updateWidget(Widget widget);

	void deleteWidgetById(long id);
}
