/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
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
package com.qaprosoft.zafira.services.services.application.integration.impl;

import static com.qaprosoft.zafira.models.db.Setting.SettingType.SLACK_WEB_HOOK_URL;
import static com.qaprosoft.zafira.models.db.Setting.Tool.SLACK;
import static com.qaprosoft.zafira.services.services.application.integration.context.SlackContext.SlackAdditionalProperty.AUTHOR;
import static com.qaprosoft.zafira.services.services.application.integration.context.SlackContext.SlackAdditionalProperty.IMAGE;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import com.qaprosoft.zafira.services.util.URLResolver;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.emails.TestRunResultsEmail;
import com.qaprosoft.zafira.services.services.application.integration.context.SlackContext;

import in.ashwanthkumar.slack.webhook.SlackAttachment;
import in.ashwanthkumar.slack.webhook.SlackAttachment.Field;
import in.ashwanthkumar.slack.webhook.SlackMessage;
import org.springframework.stereotype.Component;

@Component
public class SlackService extends AbstractIntegration<SlackContext> {

    private final static String RESULTS_PATTERN = "Passed: %d, Failed: %d, Known Issues: %d, Skipped: %d";
    private final static String INFO_PATTERN = "%1$s\n<%2$s|Open in Zafira>  |  <%3$s|Open in Jenkins>";
    private final static String ON_FINISH_PATTERN = "Test run #%1$d has been completed after %2$s with status %3$s\n";
    private final static String REVIEWED_PATTERN = "Test run #%1$d has been reviewed. Status: %2$s\n";

    private final String image;
    private final String author;
    private final URLResolver urlResolver;
    private final JenkinsService jenkinsService;
    private final SettingsService settingsService;
    private final CryptoService cryptoService;

    public SlackService(URLResolver urlResolver,
                        JenkinsService jenkinsService,
                        SettingsService settingsService,
                        CryptoService cryptoService,
                        @Value("${zafira.slack.image}") String image,
                        @Value("${zafira.slack.author}") String author) {
        super(settingsService, cryptoService, SLACK, SlackContext.class);
        this.urlResolver = urlResolver;
        this.jenkinsService = jenkinsService;
        this.settingsService = settingsService;
        this.cryptoService = cryptoService;
        this.image = image;
        this.author = author;
    }

    @Override
    public Map<SlackContext.SlackAdditionalProperty, String> additionalContextProperties() {
        Map<SlackContext.SlackAdditionalProperty, String> additionalProperties = new HashMap<>();
        additionalProperties.put(AUTHOR, author);
        additionalProperties.put(IMAGE, image);
        return additionalProperties;
    }

    @Override
    public boolean isConnected() {
        return mapContext(context -> {
            boolean result = false;
            try {
                context.getSlack().push(new SlackMessage(StringUtils.EMPTY));
                result = true;
            } catch (IOException e) {
                if (((HttpResponseException) e).getStatusCode() != HttpStatusCodes.STATUS_CODE_NOT_FOUND) {
                    result = true;
                }
            }
            return result;
        }).orElse(false);
    }

    public void sendStatusOnFinish(TestRun testRun) {
        String onFinishMessage = String.format(ON_FINISH_PATTERN, testRun.getId(), countElapsedInSMH(testRun.getElapsed()), TestRunResultsEmail.buildStatusText(testRun));
        sendNotification(testRun, onFinishMessage);
    }

    public void sendStatusReviewed(TestRun testRun) {
        String reviewedMessage = String.format(REVIEWED_PATTERN, testRun.getId(), TestRunResultsEmail.buildStatusText(testRun));
        sendNotification(testRun, reviewedMessage);
    }

    private void sendNotification(TestRun tr, String customizedMessage) {
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
                    context().setSlack(context().getSlack().sendToChannel(channel));
                    context().getSlack().push(attachment);
                } catch (IOException e) {
                    LOGGER.error("Unable to push Slack notification");
                }
            });
        }
    }

    private SlackAttachment generateSlackAttachment(String mainMessage, String messageResults, String attachmentColor, String comments) {
        SlackAttachment slackAttachment = new SlackAttachment("");
        slackAttachment
                .preText(mainMessage)
                .color(attachmentColor)
                .addField(new Field("Test Results", messageResults, false))
                .fallback(mainMessage + "\n" + messageResults);
        if (comments != null) {
            slackAttachment.addField(new Field("Comments", comments, false));
        }
        return slackAttachment;
    }


    public String getWebhook() {
        String wH = null;
        Setting slackWebHookURL = settingsService.getSettingByType(SLACK_WEB_HOOK_URL);
        if (slackWebHookURL != null) {
            if (slackWebHookURL.isEncrypted()) {
                try {
                    slackWebHookURL.setValue(cryptoService.decrypt(slackWebHookURL.getValue()));
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
            wH = slackWebHookURL.getValue();
        }
        if (wH != null && !StringUtils.isEmpty(wH)) {
            return wH;
        }
        return wH;
    }

    private String buildRunInfo(TestRun tr) {
        StringBuilder sbInfo = new StringBuilder();
        sbInfo.append(tr.getProject().getName());
        Map<String, String> jenkinsParams = jenkinsService.getBuildParametersMap(tr.getJob(), tr.getBuildNumber()).orElse(null);
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

}
