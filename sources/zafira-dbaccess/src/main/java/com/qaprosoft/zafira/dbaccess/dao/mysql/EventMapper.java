package com.qaprosoft.zafira.dbaccess.dao.mysql;

import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.models.db.Event;
import com.qaprosoft.zafira.models.db.Event.Type;

public interface EventMapper
{
	void createEvent(Event event);

	Event getEventById(long id);

	Event getEventByTypeAndTestRunIdAndTestId(@Param("type") Type type, @Param("testRunId") String testRunId, @Param("testId") String testId);

	void updateEvent(Event event);

	void deleteEventById(long id);
}
