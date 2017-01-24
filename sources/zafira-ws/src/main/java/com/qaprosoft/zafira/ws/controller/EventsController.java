package com.qaprosoft.zafira.ws.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.Event;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.EventService;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping("events")
public class EventsController extends AbstractController
{
	@Autowired
	private EventService eventService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Event createEvent(@RequestBody @Valid Event event) throws ServiceException
	{
		eventService.createEvent(event);
		return event;
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="received", method = RequestMethod.PUT)
	public void markEventReceived(@RequestBody @Valid Event event) throws ServiceException
	{
		eventService.markEventReceived(event.getType(), event.getTestRunId(), event.getTestId());
	}
}
