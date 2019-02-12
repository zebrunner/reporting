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
package com.qaprosoft.zafira.services.services.application.jmx;

import static com.qaprosoft.zafira.models.db.Setting.Tool.JENKINS;
import static com.qaprosoft.zafira.models.dto.BuildParameterType.BuildParameterClass.BOOLEAN;
import static com.qaprosoft.zafira.models.dto.BuildParameterType.BuildParameterClass.HIDDEN;
import static com.qaprosoft.zafira.models.dto.BuildParameterType.BuildParameterClass.STRING;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.ExtractHeader;
import com.offbytwo.jenkins.model.FolderJob;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueReference;
import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.dto.BuildParameterType;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.jmx.context.JenkinsContext;

@ManagedResource(objectName = "bean:name=jenkinsService", description = "Jenkins init Managed Bean", currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200, persistLocation = "foo", persistName = "bar")
public class JenkinsService implements IJMXService<JenkinsContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JenkinsService.class);

    private static final String[] REQUIRED_ARGS = new String[] {"scmURL", "scmBranch", "suite", "args"};

    private final String FOLDER_REGEX = ".+job\\/.+\\/job.+";

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CryptoService cryptoService;

    @Override
    public void init() {
        String url = null;
        String username = null;
        String passwordOrApiToken = null;
        String launcherJobName = null;

        try {
            List<Setting> jenkinsSettings = settingsService.getSettingsByTool(JENKINS);
            for (Setting setting : jenkinsSettings) {
                if (setting.isEncrypted()) {
                    setting.setValue(cryptoService.decrypt(setting.getValue()));
                }
                switch (Setting.SettingType.valueOf(setting.getName())) {
                case JENKINS_URL:
                    url = setting.getValue();
                    break;
                case JENKINS_USER:
                    username = setting.getValue();
                    break;
                case JENKINS_API_TOKEN_OR_PASSWORD:
                    passwordOrApiToken = setting.getValue();
                    break;
                case JENKINS_LAUNCHER_JOB_NAME:
                    launcherJobName = setting.getValue();
                    break;
                default:
                    break;
                }
            }
            init(url, username, passwordOrApiToken, launcherJobName);
        } catch (Exception e) {
            LOGGER.error("Setting does not exist", e);
        }
    }

    @ManagedOperation(description = "Change Jenkins initialization")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "url", description = "Jenkins url"),
            @ManagedOperationParameter(name = "username", description = "Jenkins username"),
            @ManagedOperationParameter(name = "passwordOrApiToken", description = "Jenkins passwordOrApiToken or api token"),
            @ManagedOperationParameter(name = "launcherJobName", description = "Jenkins launcher job name") })
    public void init(String url, String username, String passwordOrApiToken, String launcherJobName) {
        try {
            if (!StringUtils.isEmpty(url) && !StringUtils.isEmpty(username) && !StringUtils.isEmpty(passwordOrApiToken)) {
                putContext(JENKINS, new JenkinsContext(url, username, passwordOrApiToken, launcherJobName));
            }
        } catch (Exception e) {
            LOGGER.error("Unable to initialize Jenkins integration: " + e.getMessage());
        }
    }

    public boolean rerunJob(Job ciJob, Integer buildNumber, boolean rerunFailures) {
        boolean success = false;
        try {
            JobWithDetails job = getJobWithDetails(ciJob);

            Map<String, String> params = job.getBuildByNumber(buildNumber).details().getParameters();
            params.put("rerun_failures", Boolean.toString(rerunFailures));
            params.replace("debug", "false");
            QueueReference reference = job.build(params, true);
            success = checkReference(reference);
        } catch (Exception e) {
            LOGGER.error("Unable to rerun Jenkins job:  " + e.getMessage());
        }
        return success;
    }

    public boolean debug(Job ciJob, Integer buildNumber) {
        boolean success = false;
        try {
            JobWithDetails job = getJobWithDetails(ciJob);
            Map<String, String> params = job.getBuildByNumber(buildNumber).details().getParameters();
            params.replace("debug", "true");
            params.replace("rerun_failures", "true");
            params.replace("thread_count", "1");
            QueueReference reference = job.build(params, true);
            success = checkReference(reference);
        } catch (Exception e) {
            LOGGER.error("Unable to rerun Jenkins job:  " + e.getMessage());
        }
        return success;
    }
    
    public boolean buildJob(Job job, Map<String, String> jobParameters) {
        boolean success = false;
        try {
            JobWithDetails ciJob = getJobWithDetails(job);
            QueueReference reference = ciJob.build(jobParameters, true);
            success = checkReference(reference);
        } catch (Exception e) {
            LOGGER.error("Unable to run Jenkins job:  " + e.getMessage());
        }
        return success;
    }

    public boolean abortJob(Job ciJob, Integer buildNumber) {
        boolean success = false;
        try {
            JobWithDetails job = getJobWithDetails(ciJob);
            QueueReference reference = stop(job, buildNumber);
            success = checkReference(reference);
            if (!checkReference(reference)) {
                reference = terminate(job, buildNumber);
                success = checkReference(reference);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to abort Jenkins job:  " + e.getMessage());
        }
        return success;
    }

    private QueueReference stop(JobWithDetails job, Integer buildNumber) throws IOException {
        ExtractHeader location = (ExtractHeader) job.getClient().post(job.getUrl() + buildNumber + "/stop",
                null, ExtractHeader.class);
        return new QueueReference(location.getLocation());
    }

    private QueueReference terminate(JobWithDetails job, Integer buildNumber) throws IOException {
        ExtractHeader location = (ExtractHeader) job.getClient().post(job.getUrl() + buildNumber + "/term",
                null, ExtractHeader.class);
        return new QueueReference(location.getLocation());
    }

    private boolean checkReference(QueueReference reference) {
        return reference != null && !StringUtils.isEmpty(reference.getQueueItemUrlPart());
    }

    public List<BuildParameterType> getBuildParameters(Job ciJob, Integer buildNumber) {
        List<BuildParameterType> jobParameters = null;
        try {
            JobWithDetails job = getJobWithDetails(ciJob);
            jobParameters = getJobParameters(job.getBuildByNumber(buildNumber).details().getActions());
            BuildParameterType buildParameter = new BuildParameterType(HIDDEN, "ci_run_id",
                    UUID.randomUUID().toString());
            jobParameters.add(buildParameter);
        } catch (Exception e) {
            LOGGER.error("Unable to get job:  " + e.getMessage());
        }
        return jobParameters;
    }

    public Map<String, String> getBuildParametersMap(Job ciJob, Integer buildNumber) {
        Map<String, String> jobParameters = null;
        try {
            JobWithDetails job = getJobWithDetails(ciJob);
            jobParameters = job.getBuildByNumber(buildNumber).details().getParameters();
            jobParameters.put("ci_run_id", UUID.randomUUID().toString());
        } catch (Exception e) {
            LOGGER.error("Unable to get job:  " + e.getMessage());
        }
        return jobParameters;
    }

    public Map<Integer, String> getBuildConsoleOutputHtml(Job ciJob, Integer buildNumber, Integer stringsCount,
            Integer fullCount) {
        Map<Integer, String> result = new HashMap<>();
        try {
            JobWithDetails jobWithDetails = getJobWithDetails(ciJob);
            BuildWithDetails buildWithDetails = jobWithDetails.getBuildByNumber(buildNumber).details();
            buildWithDetails.isBuilding();
            result = getLastLogStringsByCount(buildWithDetails.getConsoleOutputHtml(), stringsCount, fullCount);
            if (!buildWithDetails.isBuilding()) {
                result.put(-1, buildWithDetails.getDisplayName());
            }
        } catch (IOException e) {
            LOGGER.error("Unable to get console output text: " + e.getMessage());
        }
        return result;
    }


    private JobWithDetails getJobWithDetails(Job ciJob) throws IOException {
        JobWithDetails job;
        if (ciJob.getJobURL().matches(FOLDER_REGEX)) {
            String jobUrl = ciJob.getJobURL();
            String folderUrl = jobUrl.substring(0, jobUrl.lastIndexOf("/job/"));
            String[] folderNameValues = jobUrl.split("/job/");
            String folderName = folderNameValues[folderNameValues.length - 2];
            Optional<FolderJob> folder = getServer().getFolderJob(new com.offbytwo.jenkins.model.Job(folderName, folderUrl));
            job = getServer().getJob(folder.get(), ciJob.getName());
        } else {
            job = getServer().getJob(ciJob.getName());
        }
        return job;
    }

    private Map<Integer, String> getLastLogStringsByCount(String log, Integer count, Integer fullCount) {
        Map<Integer, String> logMap = new HashMap<>();
        int zero = 0;
        String[] strings = log.split("\n");
        count = strings.length < count ? strings.length : count;
        if (fullCount != zero) {
            count = strings.length > fullCount ? strings.length - fullCount : zero;
        }
        logMap.put(strings.length,
                String.join("\n", Arrays.copyOfRange(strings, strings.length - count, strings.length)));
        return logMap;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<BuildParameterType> getJobParameters(List actions) {
        Collection parameters = Collections2.filter(actions,
                (Predicate<Map<String, Object>>) action -> action.containsKey("parameters"));
        List<BuildParameterType> params = new ArrayList<>();
        if (parameters != null && !parameters.isEmpty()) {
            for (Object o : ((List) ((Map) parameters.toArray()[0]).get("parameters"))) {
                BuildParameterType buildParameter = new BuildParameterType();
                Map<String, Object> param = (Map) o;
                String name = String.valueOf(param.get("name"));
                String value = String.valueOf(param.get("value"));
                String buildParamClass = String.valueOf(param.get("_class"));
                buildParameter.setName(name);
                buildParameter.setValue(value);
                if (buildParamClass.contains("Hide")) {
                    buildParameter.setParameterClass(HIDDEN);
                } else if (buildParamClass.contains("String")) {
                    buildParameter.setParameterClass(STRING);
                } else if (buildParamClass.contains("Boolean")) {
                    buildParameter.setParameterClass(BOOLEAN);
                }
                if (!name.equals("ci_run_id"))
                    params.add(buildParameter);
            }
        }
        return params;
    }

    public Job getJob(String jobName) {
        Job job = null;
        try {
            JobWithDetails jobWithDetails = getServer().getJob(jobName);
            if(jobWithDetails != null && jobWithDetails.getUrl() != null) {
                job = new Job(jobName, jobWithDetails.getUrl());
            }
        } catch (IOException e) {
            LOGGER.error("Unable to get job by name '" + jobName + "'. " + e.getMessage(), e);
        }
        return job;
    }

    public static boolean checkArguments(Map<String, String> args) {
        return Arrays.stream(REQUIRED_ARGS).filter(arg -> args.get(arg) == null).collect(Collectors.toList()).size() == 0;
    }

    public static String[] getRequiredArgs() {
        return REQUIRED_ARGS;
    }

    @Override
    public boolean isConnected() {
        return getServer() != null && getServer().isRunning();
    }

    @ManagedAttribute(description = "Get jenkins server")
    public JenkinsServer getServer() {
        return getContext(JENKINS) != null ? getContext(JENKINS).getJenkinsServer() : null;
    }

    public JenkinsContext getContext() {
        return getContext(JENKINS);
    }
}