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
package com.qaprosoft.zafira.service.integration.tool.impl;

import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.service.email.TestRunResultsEmail;
import com.qaprosoft.zafira.service.integration.IntegrationService;
import com.qaprosoft.zafira.service.integration.tool.AbstractIntegrationService;
import com.qaprosoft.zafira.service.integration.tool.adapter.slack.SlackAdapter;
import com.qaprosoft.zafira.service.integration.tool.proxy.SlackProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SlackService extends AbstractIntegrationService<SlackAdapter> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackService.class);

    private final static String ON_FINISH_PATTERN = "Test run #%1$d has been completed after %2$s with status %3$s\n";
    private final static String REVIEWED_PATTERN = "Test run #%1$d has been reviewed. Status: %2$s\n";

    private final IntegrationService integrationService;

    public SlackService(IntegrationService integrationService, SlackProxy slackProxy, IntegrationService integrationService1) {
        super(integrationService, slackProxy, "SLACK");
        this.integrationService = integrationService1;
    }

    public void sendStatusOnFinish(TestRun testRun) {
        Integration slack = integrationService.retrieveDefaultByIntegrationTypeName("SLACK");
        if (slack.isEnabled()) {
            String readableTime = asReadableTime(testRun.getElapsed());
            String statusText = TestRunResultsEmail.buildStatusText(testRun);
            String onFinishMessage = String.format(ON_FINISH_PATTERN, testRun.getId(), readableTime, statusText);
            SlackAdapter adapter = getAdapterByIntegrationId(null);
            adapter.sendNotification(testRun, onFinishMessage);
        }
        // otherwise - do nothing
        LOGGER.info(String.format("Slack notification for test run %d is not sent: integration disabled", testRun.getId()));
    }

    public void sendStatusReviewed(TestRun testRun) {
        String statusText = TestRunResultsEmail.buildStatusText(testRun);
        String reviewedMessage = String.format(REVIEWED_PATTERN, testRun.getId(), statusText);
        SlackAdapter adapter = getAdapterByIntegrationId(null);
        adapter.sendNotification(testRun, reviewedMessage);
    }

    public String getWebhook() {
        SlackAdapter adapter = getAdapterByIntegrationId(null);
        return adapter.getWebhook();
    }

    /**
     * Converts to execution time in seconds to user-friendly form such as 10 h 13 min 33 sec
     * @param elapsed elapsed time in seconds
     * @return formatted value
     */
    private String asReadableTime(Integer elapsed) {
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
