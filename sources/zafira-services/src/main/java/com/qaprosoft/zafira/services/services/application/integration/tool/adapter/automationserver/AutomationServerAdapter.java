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
package com.qaprosoft.zafira.services.services.application.integration.tool.adapter.automationserver;

import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.dto.BuildParameterType;
import com.qaprosoft.zafira.models.dto.JobResult;
import com.qaprosoft.zafira.services.services.application.integration.tool.adapter.IntegrationGroupAdapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AutomationServerAdapter extends IntegrationGroupAdapter {

    JobResult buildJob(Job job, Map<String, String> jobParameters);

    JobResult buildJob(String jobUrl, Map<String, String> jobParameters);

    JobResult abortJob(Job ciJob, Integer buildNumber);

    JobResult abortJob(String jobUrl, Integer buildNumber);

    JobResult buildScannerJob(String repositoryName, Map<String, String> jobParameters, boolean rescan);

    JobResult abortScannerJob(String repositoryName, Integer buildNumber, boolean rescan);

    String buildLauncherJobUrl();

    List<BuildParameterType> getBuildParameters(Job ciJob, Integer buildNumber);

    Optional<Map<String, String>> getBuildParametersMap(Job ciJob, Integer buildNumber);

    Map<Integer, String> getBuildConsoleOutputHtml(Job ciJob, Integer buildNumber, Integer stringsCount, Integer fullCount);

    Integer getBuildNumber(String queueItemUrl);

    Job getJobByUrl(String jobUrl);

    String getUrl();

    String getFolder();

    boolean checkArguments(Map<String, String> args);

    String buildJobUrl(String repositoryName, boolean rescan);

}
