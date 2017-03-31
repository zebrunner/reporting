package com.qaprosoft.zafira.ws.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import springfox.documentation.annotations.ApiIgnore;

import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.push.TestRunPush;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.TestRunService;
import com.qaprosoft.zafira.services.services.slack.SlackService;

@Controller
@ApiIgnore
@RequestMapping("slack")
@Secured(
{ "ROLE_ADMIN" })
public class SlackController extends AbstractController
{
	@Autowired
	private SlackService slackService;

	@Autowired
	private TestRunService testRunService;

	@Autowired
	private SimpMessagingTemplate websocketTemplate;

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "triggerReviewNotif/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody void triggerReviewNotif(@PathVariable(value = "id") long id) throws ServiceException,
			IOException, InterruptedException
	{
		TestRun tr = testRunService.getTestRunByIdFull(id);
		if (!tr.isReviewed())
		{
			tr.setReviewed(true);
			slackService.sendReviwedStatus(tr);
			tr = testRunService.updateTestRun(tr);
			websocketTemplate.convertAndSend(WEBSOCKET_PATH, new TestRunPush(tr));
		}
	}
}
