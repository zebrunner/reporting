package com.qaprosoft.zafira.ws.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.TestRunService;
import com.qaprosoft.zafira.services.services.slack.SlackService;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping("slack")
public class SlackController extends AbstractController
{
	@Autowired
	private SlackService slackService;

	@Autowired
	private TestRunService testRunService;

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "triggerReviewNotif/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody void triggerReviewNotif(@PathVariable(value = "id") long id) throws ServiceException,
			IOException, InterruptedException
	{
		TestRun tr = testRunService.getTestRunByIdFull(id);
		slackService.sendReviwedStatus(tr);
	}

}
