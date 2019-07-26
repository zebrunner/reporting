/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.dto.SlackReplyType;
import com.qaprosoft.zafira.services.services.application.TestRunService;
import com.qaprosoft.zafira.services.services.application.integration.impl.SlackService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api("Slack API")
@CrossOrigin
@RequestMapping(path = "api/slack", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class SlackAPIController extends AbstractController {

    @Autowired
    private SlackService slackService;

    @Autowired
    private TestRunService testRunService;

    @ResponseStatusDetails
    @ApiOperation(value = "Send notification on testrun review", nickname = "sendReviewNotification", httpMethod = "GET")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/testrun/{id}/review")
    public void sendOnReviewNotification(@PathVariable("id") long id) {
        TestRun testRun = testRunService.getTestRunByIdFull(id);
        slackService.sendNotificationsOnReview(testRun);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Send notification on testrun finish", nickname = "sendOnFinishNotification", httpMethod = "GET")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("/testrun/{ciRunId}/finish")
    public void sendOnFinishNotification(
            @PathVariable("ciRunId") String ciRunId,
            @RequestParam(value = "channels", required = false) String channels) {
        TestRun testRun = testRunService.getTestRunByCiRunIdFull(ciRunId);
        testRun.setSlackChannels(channels);
        testRunService.updateTestRun(testRun);
        slackService.sendNotificationsOnFinish(testRun);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Send reply to message thread", nickname = "sendReplyToMessageThread", httpMethod = "POST")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/thread/reply")
    public void sendReplyToMessageThread(@RequestBody SlackReplyType slackReplyType){
        String channelName = slackReplyType.getChannelName();
        String messageTs = slackReplyType.getMessageTs();
        String reply = slackReplyType.getReply();
        slackService.sendReplyToThread(channelName, messageTs, reply);
    }
}
