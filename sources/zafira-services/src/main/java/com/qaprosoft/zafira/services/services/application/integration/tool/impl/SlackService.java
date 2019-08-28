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
package com.qaprosoft.zafira.services.services.application.integration.tool.impl;

import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.services.services.application.emails.TestRunResultsEmail;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.AbstractIntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.slack.SlackServiceAdapter;
import org.springframework.stereotype.Component;

@Component
public class SlackService extends AbstractIntegrationService<SlackServiceAdapter> {

    private final static String ON_FINISH_PATTERN = "Test run #%1$d has been completed after %2$s with status %3$s\n";
    private final static String REVIEWED_PATTERN = "Test run #%1$d has been reviewed. Status: %2$s\n";

    public SlackService(IntegrationService integrationService) {
        super(integrationService, "SLACK");
    }

    public void sendStatusOnFinish(TestRun testRun) {
        String onFinishMessage = String.format(ON_FINISH_PATTERN, testRun.getId(), countElapsedInSMH(testRun.getElapsed()),
                TestRunResultsEmail.buildStatusText(testRun));
        SlackServiceAdapter slackServiceAdapter = getAdapterForIntegration(null);
        slackServiceAdapter.sendNotification(testRun, onFinishMessage);
    }

    public void sendStatusReviewed(TestRun testRun) {
        String reviewedMessage = String.format(REVIEWED_PATTERN, testRun.getId(), TestRunResultsEmail.buildStatusText(testRun));
        SlackServiceAdapter slackServiceAdapter = getAdapterForIntegration(null);
        slackServiceAdapter.sendNotification(testRun, reviewedMessage);
    }

    public String getWebhook() {
        SlackServiceAdapter slackServiceAdapter = getAdapterForIntegration(null);
        return slackServiceAdapter.getWebhook();
    }

    private String countElapsedInSMH(Integer elapsed) {
        if (elapsed != null) {
            int s = elapsed % 60;
            int m = (elapsed / 60) % 60;
            int h = (elapsed / (60 * 60)) % 24;
            StringBuilder sb = new StringBuilder(String.format("%02d sec", s));
            if (m > 0)
                sb.insert(0, String.format("%02d min ", m));
            if (h > 0)
                sb.insert(0, String.format("%02d h ", h));
            return sb.toString();
        }
        return null;
    }

}