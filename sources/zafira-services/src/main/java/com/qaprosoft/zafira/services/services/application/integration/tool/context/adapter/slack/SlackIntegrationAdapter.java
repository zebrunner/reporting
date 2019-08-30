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
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.slack;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.AdapterParam;
import com.qaprosoft.zafira.services.services.application.integration.tool.impl.AutomationServerService;
import com.qaprosoft.zafira.services.util.URLResolver;
import in.ashwanthkumar.slack.webhook.SlackAttachment;
import in.ashwanthkumar.slack.webhook.SlackMessage;
import in.ashwanthkumar.slack.webhook.service.SlackService;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class SlackIntegrationAdapter extends AbstractIntegrationAdapter implements SlackServiceAdapter {

    private final static String RESULTS_PATTERN = "Passed: %d, Failed: %d, Known Issues: %d, Skipped: %d";
    private final static String INFO_PATTERN = "%1$s\n<%2$s|Open in Zafira>  |  <%3$s|Open in Jenkins>";

    private final String image;
    private final String author;
    private final String webhookUrl;
    private final SlackService slackService;
    private final URLResolver urlResolver;
    private final AutomationServerService automationServerService;

    public SlackIntegrationAdapter(Integration integration,
                                   URLResolver urlResolver,
                                   AutomationServerService automationServerService,
                                   Map<String, String> additionalProperties) {
        super(integration);

        this.urlResolver = urlResolver;
        this.automationServerService = automationServerService;

        this.image = additionalProperties.get("image");
        this.author = additionalProperties.get("author");

        this.webhookUrl = getAttributeValue(SlackParam.SLACK_WEB_HOOK_URL);
        this.slackService = new SlackService();
    }

    private enum SlackParam implements AdapterParam {
        SLACK_WEB_HOOK_URL("SLACK_WEB_HOOK_URL");

        private final String name;

        SlackParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public boolean isConnected() {
        boolean result = false;
        try {
            push(null, new SlackMessage(StringUtils.EMPTY));
            result = true;
        } catch (IOException e) {
            if (((HttpResponseException) e).getStatusCode() != HttpStatusCodes.STATUS_CODE_NOT_FOUND) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public void sendNotification(TestRun tr, String customizedMessage) {
        String channels = tr.getSlackChannels();
        if (StringUtils.isNotEmpty(channels)) {
            String zafiraUrl = urlResolver.buildWebURL() + "/tests/runs/" + tr.getId();
            String jenkinsUrl = tr.getJob().getJobURL() + "/" + tr.getBuildNumber();
            String attachmentColor = determineColor(tr);
            String mainMessage = customizedMessage + String.format(INFO_PATTERN, buildRunInfo(tr), zafiraUrl, jenkinsUrl);
            String resultsMessage = String.format(RESULTS_PATTERN, tr.getPassed(), tr.getFailed(), tr.getFailedAsKnown(), tr.getSkipped());
            SlackAttachment attachment = generateSlackAttachment(mainMessage, resultsMessage, attachmentColor, tr.getComments());
            Arrays.stream(channels.split(",")).forEach(channel -> {
                try {
                    push(channel, attachment);
                } catch (IOException e) {
                    LOGGER.error("Unable to push Slack notification");
                }
            });
        }
    }

    @Override
    public String getWebhook() {
        return webhookUrl;
    }

    private String determineColor(TestRun tr) {
        if (tr.getPassed() > 0 && tr.getFailed() == 0 && tr.getSkipped() == 0) {
            return "good";
        }
        if (tr.getPassed() == 0 && tr.getFailed() == 0 && tr.getFailedAsKnown() == 0
                && tr.getSkipped() == 0) {
            return "danger";
        }
        return "warning";
    }

    private String buildRunInfo(TestRun tr) {
        StringBuilder sbInfo = new StringBuilder();
        sbInfo.append(tr.getProject().getName());
        Map<String, String> jenkinsParams = automationServerService.getBuildParametersMap(tr.getJob(), tr.getBuildNumber()).orElse(null);
        if (jenkinsParams != null && jenkinsParams.get("groups") != null) {
            sbInfo.append("(");
            sbInfo.append(jenkinsParams.get("groups"));
            sbInfo.append(")");
        }
        sbInfo.append(" | ");
        sbInfo.append(tr.getTestSuite().getName());
        sbInfo.append(" | ");
        sbInfo.append(tr.getEnv());
        sbInfo.append(" | ");
        sbInfo.append(tr.getPlatform() == null ? "no_platform" : tr.getPlatform());
        if (tr.getAppVersion() != null) {
            sbInfo.append(" | ");
            sbInfo.append(tr.getAppVersion());
        }
        return sbInfo.toString();
    }

    private SlackAttachment generateSlackAttachment(String mainMessage, String messageResults, String attachmentColor, String comments) {
        SlackAttachment slackAttachment = new SlackAttachment("");
        slackAttachment
                .preText(mainMessage)
                .color(attachmentColor)
                .addField(new SlackAttachment.Field("Test Results", messageResults, false))
                .fallback(mainMessage + "\n" + messageResults);
        if (comments != null) {
            slackAttachment.addField(new SlackAttachment.Field("Comments", comments, false));
        }
        return slackAttachment;
    }

    private void push(String channel, SlackAttachment slackAttachment) throws IOException {
        slackService.push(webhookUrl, new SlackMessage(), author, image, channel, null, Collections.singletonList(slackAttachment));
    }

    private void push(String channel, SlackMessage message) throws IOException {
        slackService.push(webhookUrl, message, author, image, channel, null, new ArrayList<>());
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public SlackService getSlackService() {
        return slackService;
    }

}
