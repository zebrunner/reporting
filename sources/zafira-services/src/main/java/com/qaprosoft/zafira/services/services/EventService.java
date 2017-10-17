package com.qaprosoft.zafira.services.services;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.EventMapper;
import com.qaprosoft.zafira.models.db.Event;
import com.qaprosoft.zafira.models.db.Event.Type;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class EventService
{
	private static Logger LOGGER = LoggerFactory.getLogger(EventService.class);
	
	@Autowired
	public EventMapper eventMapper;
	
	@Transactional(rollbackFor = Exception.class)
	public void createEvent(Event event) throws ServiceException
	{
		eventMapper.createEvent(event);
	}
	
	@Transactional(readOnly = true)
	public Event getEventById(long id) throws ServiceException
	{
		return eventMapper.getEventById(id);
	}
	
	@Transactional(readOnly = true)
	public Event getEventByTypeAndTestRunIdAndTestId(Type type, String testRunId, String testId) throws ServiceException
	{
		return eventMapper.getEventByTypeAndTestRunIdAndTestId(type, testRunId, testId);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Event updateEvent(Event event) throws ServiceException
	{
		eventMapper.updateEvent(event);
		return event;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteEventById(long id) throws ServiceException
	{
		eventMapper.deleteEventById(id);
	}
	
	public void logEvent(Event event)
	{
		try
		{
			createEvent(event);
		}
		catch(Exception e)
		{
			LOGGER.error("Unable log event: " + e.getMessage());
		}
	}
	
	public void markEventReceived(Type type, String testRunId, String testId)
	{
		try
		{
			Event event = getEventByTypeAndTestRunIdAndTestId(type, testRunId, testId);
			if(event != null)
			{
				event.setReceived(Calendar.getInstance().getTime());
				updateEvent(event);
			}
		}
		catch(Exception e)
		{
			LOGGER.error("Unable to mark event as received: " + e.getMessage());
		}
	}
}