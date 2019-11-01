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
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.service.exception.ResourceNotFoundException;
import com.qaprosoft.zafira.service.integration.tool.impl.AutomationServerService;
import com.qaprosoft.zafira.service.integration.tool.impl.SlackService;
import com.qaprosoft.zafira.service.integration.tool.impl.TestCaseManagementService;
import com.qaprosoft.zafira.service.util.URLResolver;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.TEST_RUN_NOT_FOUND;

@Service
public class ConfigurationService {

    private static final String ERR_MSG_TEST_RUN_NOT_FOUND = "Test run with id %s can not be found";

    private final VersionService versionService;
    private final URLResolver urlResolver;
    private final AutomationServerService automationServerService;
    private final TestCaseManagementService testCaseManagementService;
    private final TestRunService testRunService;
    private final SlackService slackService;

    public ConfigurationService(
            VersionService versionService,
            URLResolver urlResolver,
            AutomationServerService automationServerService,
            TestCaseManagementService testCaseManagementService,
            TestRunService testRunService,
            SlackService slackService
    ) {
        this.versionService = versionService;
        this.urlResolver = urlResolver;
        this.automationServerService = automationServerService;
        this.testCaseManagementService = testCaseManagementService;
        this.testRunService = testRunService;
        this.slackService = slackService;
    }

    public Map<String, Object> getAppConfig() {
        return Map.of(
                "service", versionService.getServiceVersion(),
                "client", versionService.getClientVersion(),
                "service_url", urlResolver.buildWebserviceUrl()
        );
    }

    public Map<String, Object> getJenkinsConfig() {
        boolean enabledAndConnected = automationServerService.isEnabledAndConnected(null);
        return Map.of("connected", enabledAndConnected);
    }

    public Map<String, Object> getJiraConfig() {
        boolean enabledAndConnected = testCaseManagementService.isEnabledAndConnected(null);
        return Map.of("connected", enabledAndConnected);
    }

    public Map<String, Object> getSlackConfigByTestRunId(Long testRunId) {
        TestRun testRun = testRunService.getTestRunByIdFull(testRunId);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, String.format(ERR_MSG_TEST_RUN_NOT_FOUND, testRunId));
        }
        boolean available = isSlackAvailable() && StringUtils.isNotEmpty(testRun.getSlackChannels());
        return Map.of("available", available);
    }

    public Map<String, Object> getSlackConfig() {
        boolean available = isSlackAvailable();
        return Map.of("available", available);
    }

    private boolean isSlackAvailable() {
        return slackService.isEnabledAndConnected(null) && slackService.getWebhook() != null;
    }
}
