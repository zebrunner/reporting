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

import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.dto.BuildParameterType;
import com.qaprosoft.zafira.models.dto.JobResult;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.AbstractIntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.automationserver.AutomationServerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AutomationServerService extends AbstractIntegrationService<AutomationServerAdapter> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutomationServerService.class);

    private static final String[] REQUIRED_ARGS = new String[] { "scmURL", "branch", "zafiraFields" };

    public AutomationServerService(IntegrationService integrationService) {
        super(integrationService, "JENKINS");
    }

    public boolean rerunJob(Job ciJob, Integer buildNumber, boolean rerunFailures) {
        JobResult jobResult = null;
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(ciJob.getAutomationServerId());
        try {
            Map<String, String> params = automationServerAdapter.getBuildParametersMap(ciJob, buildNumber)
                                                                .orElseThrow(() -> new ForbiddenOperationException("Unable to rerun CI job"));

            params.put("rerun_failures", Boolean.toString(rerunFailures));
            params.replace("debug", "false");

            jobResult = automationServerAdapter.buildJob(ciJob, params);
        } catch (Exception e) {
            LOGGER.error("Unable to rerun Jenkins job:  " + e.getMessage());
        }
        return jobResult != null && jobResult.isSuccess();
    }

    public boolean debug(Job ciJob, Integer buildNumber) {
        JobResult jobResult = null;
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(ciJob.getAutomationServerId());
        try {
            Map<String, String> params = automationServerAdapter.getBuildParametersMap(ciJob, buildNumber)
                                                                .orElseThrow(() -> new ForbiddenOperationException("Unable to rerun CI job"));
            params.replace("debug", "true");
            params.replace("rerun_failures", "true");
            params.replace("thread_count", "1");
            jobResult = automationServerAdapter.buildJob(ciJob, params);
        } catch (Exception e) {
            LOGGER.error("Unable to rerun Jenkins job:  " + e.getMessage());
        }
        return jobResult != null && jobResult.isSuccess();
    }

    public JobResult buildJob(Job job, Map<String, String> jobParameters) {
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(job.getAutomationServerId());
        return automationServerAdapter.buildJob(job, jobParameters);
    }

    public JobResult buildScannerJob(String repositoryName, Map<String, String> jobParameters, boolean rescan) {
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(null);
        String jobUrl = automationServerAdapter.buildJobUrl(repositoryName, rescan);
//        LOGGER.error("Job parameters: " + jobParameters);
        return automationServerAdapter.buildJob(jobUrl, jobParameters);
    }

    public JobResult abortScannerJob(String repositoryName, Integer buildNumber, boolean rescan) {
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(null);
        String jobUrl = automationServerAdapter.buildJobUrl(repositoryName, rescan);
        return automationServerAdapter.abortJob(jobUrl, buildNumber);
    }

    public JobResult abortJob(Job ciJob, Integer buildNumber) {
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(null);
        return automationServerAdapter.abortJob(ciJob, buildNumber);
    }

    public List<BuildParameterType> getBuildParameters(Job ciJob, Integer buildNumber) {
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(null);
        return automationServerAdapter.getBuildParameters(ciJob, buildNumber);
    }

    public Optional<Map<String, String>> getBuildParametersMap(Job ciJob, Integer buildNumber) {
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(null);
        return automationServerAdapter.getBuildParametersMap(ciJob, buildNumber);
    }

    public Map<Integer, String> getBuildConsoleOutputHtml(Job ciJob, Integer buildNumber, Integer stringsCount, Integer fullCount) {
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(null);
        return automationServerAdapter.getBuildConsoleOutputHtml(ciJob, buildNumber, stringsCount, fullCount);
    }

    public Integer getBuildNumber(String queueItemUrl) {
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(null);
        return automationServerAdapter.getBuildNumber(queueItemUrl);
    }

    public Optional<Job> getJobByUrl(String jobUrl) {
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(null);
        return Optional.ofNullable(automationServerAdapter.getJobByUrl(jobUrl));
    }

    public String buildLauncherJobUrl() {
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(null);
        return automationServerAdapter.buildLauncherJobUrl();
    }

    public String getUrl() {
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(null);
        return automationServerAdapter.getUrl();
    }

    public String getFolder() {
        AutomationServerAdapter automationServerAdapter = getAdapterForIntegration(null);
        return automationServerAdapter.getFolder();
    }

    public static boolean checkArguments(Map<String, String> args) {
        return Arrays.stream(REQUIRED_ARGS).noneMatch(arg -> args.get(arg) == null);
    }

    public static String[] getRequiredArgs() {
        return REQUIRED_ARGS;
    }

}
