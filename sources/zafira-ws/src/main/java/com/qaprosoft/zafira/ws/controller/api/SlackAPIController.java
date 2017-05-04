package com.qaprosoft.zafira.ws.controller.api;

import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.TestRunService;
import com.qaprosoft.zafira.services.services.slack.SlackService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@Controller
@Api(value = "Slack API")
@CrossOrigin
@RequestMapping("api/slack")
public class SlackAPIController extends AbstractController {

    @Autowired
    private SlackService slackService;

    @Autowired
    private TestRunService testRunService;

    @Autowired
    private SimpMessagingTemplate websocketTemplate;

    @ResponseStatusDetails
    @ApiOperation(value = "Trigger review notif", nickname = "triggerReviewNotif", code = 200, httpMethod = "GET")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "triggerReviewNotif/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void triggerReviewNotif(@PathVariable(value = "id") long id) throws ServiceException,
            IOException, InterruptedException
    {
        TestRun tr = testRunService.getTestRunByIdFull(id);
        slackService.sendReviwedStatus(tr);
    }
}
