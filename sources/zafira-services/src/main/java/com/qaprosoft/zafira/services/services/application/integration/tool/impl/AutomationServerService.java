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
import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.AbstractIntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.adapter.automationserver.AutomationServerAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.proxy.AutomationServerProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AutomationServerService extends AbstractIntegrationService<AutomationServerAdapter> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutomationServerService.class);

    public AutomationServerService(IntegrationService integrationService, AutomationServerProxy automationServerProxy) {
        super(integrationService, automationServerProxy, "JENKINS");
    }

    public void rerunJob(Job job, Integer buildNumber, boolean rerunFailures) {
        String jobURL = job.getJobURL();
        AutomationServerAdapter adapter = getAdapterByIntegrationId(job.getAutomationServerId());
        Map<String, String> params = adapter.getBuildParametersMap(jobURL, buildNumber);

        params.put("rerun_failures", Boolean.toString(rerunFailures));
        params.replace("debug", "false");

        adapter.buildJob(jobURL, params);
    }

    public void debugJob(Job job, Integer buildNumber) {
        String jobURL = job.getJobURL();
        AutomationServerAdapter adapter = getAdapterByIntegrationId(job.getAutomationServerId());
        Map<String, String> params = adapter.getBuildParametersMap(job.getJobURL(), buildNumber);

        params.replace("debug", "true");
        params.replace("rerun_failures", "true");
        params.replace("thread_count", "1");

        adapter.buildJob(jobURL, params);
    }

    public void buildJob(Job job, Map<String, String> jobParameters) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(job.getAutomationServerId());
        adapter.buildJob(job.getJobURL(), jobParameters);
    }

    public JobResult buildScannerJob(String repositoryName, Map<String, String> jobParameters, boolean rescan, Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        String jobUrl = adapter.buildScannerJobUrl(repositoryName, rescan);
        return adapter.buildJob(jobUrl, jobParameters);
    }

    public void abortScannerJob(String repositoryName, Integer buildNumber, boolean rescan, Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        String jobUrl = adapter.buildScannerJobUrl(repositoryName, rescan);
        adapter.abortJob(jobUrl, buildNumber);
    }

    public void abortJob(Job ciJob, Integer buildNumber, Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        adapter.abortJob(ciJob.getJobURL(), buildNumber);
    }

    public List<BuildParameterType> getBuildParameters(Job job, Integer buildNumber, Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        return adapter.getBuildParameters(job, buildNumber);
    }

    public Map<Integer, String> getBuildConsoleOutput(Job job, Integer buildNumber, Integer stringsCount, Integer fullCount, Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        return adapter.getBuildConsoleOutput(job, buildNumber, stringsCount, fullCount);
    }

    public Integer getBuildNumber(String queueItemUrl, Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        return adapter.getBuildNumber(queueItemUrl);
    }

    public Job getJobByUrl(String jobUrl, Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        return adapter.getJobDetailsFromJenkins(jobUrl);
    }

    public String buildLauncherJobUrl(Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        return adapter.buildLauncherJobUrl();
    }

    public String getUrl(Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        return adapter.getUrl();
    }

    public String getFolder(Long automationServerId) {
        AutomationServerAdapter adapter = getAdapterByIntegrationId(automationServerId);
        return adapter.getFolder();
    }

}
