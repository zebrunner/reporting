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
package com.qaprosoft.zafira.service.integration.tool.adapter.automationserver;

import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.dto.BuildParameterType;
import com.qaprosoft.zafira.models.dto.JobResult;
import com.qaprosoft.zafira.service.integration.tool.adapter.IntegrationGroupAdapter;

import java.util.List;
import java.util.Map;

public interface AutomationServerAdapter extends IntegrationGroupAdapter {

    JobResult buildJob(String jobURL, Map<String, String> jobParameters);

    void abortJob(String jobURL, Integer buildNumber);

    String buildLauncherJobUrl();

    List<BuildParameterType> getBuildParameters(Job ciJob, Integer buildNumber);

    Map<String, String> getBuildParametersMap(String ciJobURL, Integer buildNumber);

    Map<Integer, String> getBuildConsoleOutput(Job ciJob, Integer buildNumber, Integer stringsCount, Integer fullCount);

    Integer getBuildNumber(String queueItemUrl);

    Job getJobDetailsFromJenkins(String jobUrl);

    String getUrl();

    String getFolder();

    String buildScannerJobUrl(String repositoryName, boolean rescan);

}
