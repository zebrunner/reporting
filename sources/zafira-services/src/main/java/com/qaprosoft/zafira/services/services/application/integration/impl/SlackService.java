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
package com.qaprosoft.zafira.services.services.application.integration.impl;

import com.github.seratch.jslack.api.model.Attachment;
import com.github.seratch.jslack.api.model.Field;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.services.exceptions.SlackNotificationNotSendException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.emails.TestRunResultsEmail;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import com.qaprosoft.zafira.services.services.application.integration.context.SlackContext;
import com.qaprosoft.zafira.services.util.URLResolver;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.qaprosoft.zafira.models.db.Setting.Tool.SLACK;

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

    public SlackService(URLResolver urlResolver,
                        JenkinsService jenkinsService,
                        SettingsService settingsService,
                        @Value("${zafira.slack.image}") String image,
                        @Value("${zafira.slack.author}") String author) {
        super(settingsService, SLACK, SlackContext.class);
        this.urlResolver = urlResolver;
        this.jenkinsService = jenkinsService;
        this.settingsService = settingsService;
        this.image = image;
        this.author = author;
    }

    @Override
    public boolean isConnected() {
        return mapContext(context -> {
            boolean result = false;
            try {
                WebhookResponse response = pushNotificationToChannel("", null);
                // valid response code if we test webhook with empty payload
                if (response.getCode() == 400) {
                    result = true;
                } else {
                    throw new SlackNotificationNotSendException("Unable to push Slack notification: " + response.getCode() + " " + response.getMessage());
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            return result;
        }).orElse(false);
    }

    public void sendNotificationsOnFinish(TestRun testRun) {
        if (StringUtils.isEmpty(testRun.getSlackChannels())) {
            throw new SlackNotificationNotSendException("No slack channels provided for testRun");
        }
        String onFinishMessage = String.format(ON_FINISH_PATTERN, testRun.getId(), LocalTime.ofSecondOfDay(testRun.getElapsed()),
                TestRunResultsEmail.buildStatusText(testRun));
        Attachment attachment = getNotificationAttachment(testRun, onFinishMessage);
        String channels = testRun.getSlackChannels();
        sendNotificationsToChannels(channels, Collections.singletonList(attachment));
    }

    public void sendNotificationsOnReview(TestRun testRun) {
        if (StringUtils.isEmpty(testRun.getSlackChannels())) {
            throw new SlackNotificationNotSendException("No slack channels provided for testRun");
        }
        String onReviewMessage = String.format(REVIEWED_PATTERN, testRun.getId(), TestRunResultsEmail.buildStatusText(testRun));
        Attachment attachment = getNotificationAttachment(testRun, onReviewMessage);
        String channels = testRun.getSlackChannels();
        sendNotificationsToChannels(channels, Collections.singletonList(attachment));
    }

    private void sendNotificationsToChannels(String channels, List<Attachment> attachments) {
        Arrays.stream(channels.split(","))
              .forEach(channel -> pushNotificationToChannel(channel, attachments));
    }

    private WebhookResponse pushNotificationToChannel(String channel, List<Attachment> slackAttachments) {
        String webHookUrl = context().getWebHookUrl();
        Payload payload = Payload.builder()
                                 .channel(channel)
                                 .attachments(slackAttachments)
                                 .build();
        WebhookResponse response;
        try {
            response = context().getSlack().send(webHookUrl, payload);
        } catch (IOException e) {
            throw new SlackNotificationNotSendException(e.getMessage());
        }
        return response;
    }

    private Attachment getNotificationAttachment(TestRun testRun, String notificationCauseMessage) {
        String headerMessage = getNotificationMessage(testRun, notificationCauseMessage);
        String testRunResultsMessage = String.format(RESULTS_PATTERN, testRun.getPassed(), testRun.getFailed(), testRun.getFailedAsKnown(), testRun.getSkipped());
        String fullMessage = headerMessage + "\n" + testRunResultsMessage;
        String attachmentColor = getAttachmentColor(testRun);

        List<Field> fields = getFields(buildField("Test Results", testRunResultsMessage), null);
        Attachment attachment = buildAttachment(headerMessage, fullMessage, attachmentColor, fields);

        String comments = testRun.getComments();
        if (comments != null) {
            getFields(buildField("Comments", comments), attachment.getFields());
        }
        return attachment;
    }

    private String getNotificationMessage(TestRun testRun, String notificationCauseMessage) {
        String zafiraUrl = urlResolver.buildWebURL() + "/tests/runs/" + testRun.getId();
        String jenkinsUrl = testRun.getJob().getJobURL() + "/" + testRun.getBuildNumber();
        String testRunHeaderInfoMessage = getTestRunInfoMessage(testRun);
        return notificationCauseMessage + String.format(INFO_PATTERN, testRunHeaderInfoMessage, zafiraUrl, jenkinsUrl);
    }

    private List<Field> getFields(Field testResultsField, List<Field> fields) {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        fields.add(testResultsField);
        return fields;
    }

    private Attachment buildAttachment(String mainMessage, String fullMessage, String attachmentColor, List<Field> fields) {
        return Attachment.builder()
                         .pretext(mainMessage)
                         .color(attachmentColor)
                         .fields(fields)
                         .fallback(fullMessage)
                         .authorName(author)
                         .imageUrl(image)
                         .build();
    }

    private Field buildField(String name, String value) {
        return Field.builder()
                    .title(name)
                    .value(value)
                    .valueShortEnough(false)
                    .build();
    }

    /**
     * Concats info about TestRun
     * Ex. UNKNOWN | Carina Demo Tests - API sample tests | DEMO | API
     *
     * @param testRun
     * @return
     */
    private String getTestRunInfoMessage(TestRun testRun) {
        StringBuilder sbInfo = new StringBuilder();
        sbInfo.append(testRun.getProject().getName());
        Map<String, String> jenkinsParams = jenkinsService.getBuildParametersMap(testRun.getJob(), testRun.getBuildNumber()).orElse(null);
        if (jenkinsParams != null && jenkinsParams.get("groups") != null) {
            sbInfo.append("(");
            sbInfo.append(jenkinsParams.get("groups"));
            sbInfo.append(")");
        }
        sbInfo.append(" | ");
        sbInfo.append(testRun.getTestSuite().getName());
        sbInfo.append(" | ");
        sbInfo.append(testRun.getEnv());
        sbInfo.append(" | ");
        sbInfo.append(testRun.getPlatform() == null ? "no_platform" : testRun.getPlatform());
        if (testRun.getAppVersion() != null) {
            sbInfo.append(" | ");
            sbInfo.append(testRun.getAppVersion());
        }
        return sbInfo.toString();
    }

    private String getAttachmentColor(TestRun testRun) {
        if (testRun.getPassed() > 0 && testRun.getFailed() == 0 && testRun.getSkipped() == 0) {
            return "good";
        }
        if (testRun.getPassed() == 0 && testRun.getFailed() == 0 && testRun.getFailedAsKnown() == 0
                && testRun.getSkipped() == 0) {
            return "danger";
        }
        return "warning";
    }



}
