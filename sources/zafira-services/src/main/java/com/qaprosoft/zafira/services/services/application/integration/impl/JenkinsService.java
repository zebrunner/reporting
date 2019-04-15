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

import static com.qaprosoft.zafira.models.db.Setting.Tool.JENKINS;
import static com.qaprosoft.zafira.models.dto.BuildParameterType.BuildParameterClass.BOOLEAN;
import static com.qaprosoft.zafira.models.dto.BuildParameterType.BuildParameterClass.HIDDEN;
import static com.qaprosoft.zafira.models.dto.BuildParameterType.BuildParameterClass.STRING;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.ExtractHeader;
import com.offbytwo.jenkins.model.FolderJob;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueReference;
import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.dto.BuildParameterType;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.context.JenkinsContext;
import org.springframework.stereotype.Component;

@Component
public class JenkinsService extends AbstractIntegration<JenkinsContext> {

    private static final String[] REQUIRED_ARGS = new String[] {"scmURL", "branch", "overrideFields"};

    private final String FOLDER_REGEX = ".+job\\/.+\\/job.+";

    public JenkinsService(SettingsService settingsService, CryptoService cryptoService) {
        super(settingsService, cryptoService, JENKINS, JenkinsContext.class);
    }

    public boolean rerunJob(Job ciJob, Integer buildNumber, boolean rerunFailures) {
        boolean success = false;
        try {
            JobWithDetails job = getJobWithDetails(ciJob).orElseThrow(() ->
                    new ForbiddenOperationException("Unable to rerun CI job"));
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
            JobWithDetails job = getJobWithDetails(ciJob).orElseThrow(() ->
                    new ForbiddenOperationException("Unable to debug CI job"));
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
            JobWithDetails ciJob = getJobWithDetails(job).orElseThrow(() ->
                    new ForbiddenOperationException("Unable to build CI job"));
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
            JobWithDetails job = getJobWithDetails(ciJob).orElseThrow(() ->
                    new ForbiddenOperationException("Unable to abord CI job"));
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
            JobWithDetails job = getJobWithDetails(ciJob).orElseThrow(() ->
                    new ForbiddenOperationException("Unable to retrieve build parameters"));
            jobParameters = getJobParameters(job.getBuildByNumber(buildNumber).details().getActions());
            BuildParameterType buildParameter = new BuildParameterType(HIDDEN, "ci_run_id",
                    UUID.randomUUID().toString());
            jobParameters.add(buildParameter);
        } catch (Exception e) {
            LOGGER.error("Unable to get job:  " + e.getMessage());
        }
        return jobParameters;
    }

    public Optional<Map<String, String>> getBuildParametersMap(Job ciJob, Integer buildNumber) {
        Map<String, String> jobParameters = null;
        try {
            Optional<JobWithDetails> nullableJob = getJobWithDetails(ciJob);
            if(! nullableJob.isPresent()) {
                return Optional.empty();
            }
            jobParameters = nullableJob.get().getBuildByNumber(buildNumber).details().getParameters();
            jobParameters.put("ci_run_id", UUID.randomUUID().toString());
        } catch (Exception e) {
            LOGGER.error("Unable to get job:  " + e.getMessage());
        }
        return Optional.ofNullable(jobParameters);
    }

    public Map<Integer, String> getBuildConsoleOutputHtml(Job ciJob, Integer buildNumber, Integer stringsCount,
            Integer fullCount) {
        Map<Integer, String> result = new HashMap<>();
        try {
            JobWithDetails jobWithDetails = getJobWithDetails(ciJob).orElseThrow(() ->
                    new ForbiddenOperationException("Unable to retrieve console logs"));
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

    private Optional<JobWithDetails> getJobWithDetails(Job ciJob) {
        return ciJob.getJobURL().matches(FOLDER_REGEX) ? getJobByURL(ciJob.getJobURL()) : getJobByName(ciJob.getName());
    }

    private Optional<JobWithDetails> getJobByName(String jobName) {
        return mapContext(context -> {
            JobWithDetails job;
            try {
                job = context.getJenkinsServer().getJob(jobName);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return job;
        });
    }

    private Optional<JobWithDetails> getJobByURL(String jobUrl) {
        return mapContext(context -> {
            JobWithDetails job;
            try {
                String folderUrl = jobUrl.substring(0, jobUrl.lastIndexOf("job/"));
                Path path = Paths.get(jobUrl);
                String jobName = path.getName(path.getNameCount() - 1).toString();
                String folderName = path.getName(path.getNameCount() - 3).toString();
                com.google.common.base.Optional<FolderJob> folder = context.getJenkinsServer().getFolderJob(new com.offbytwo.jenkins.model.Job(folderName, folderUrl));
                job = context.getJenkinsServer().getJob(folder.orNull(), jobName);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return job;
        });
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

    public Optional<Job> getJobByUrl(String jobUrl) {
        return mapContext(context -> {
            Job job = null;
            JobWithDetails jobWithDetails = getJobByURL(jobUrl).orElse(null);
            if(jobWithDetails != null && jobWithDetails.getUrl() != null) {
                job = new Job(jobWithDetails.getDisplayName(), jobWithDetails.getUrl().replaceAll("/$",""));
            }
            return job;
        });
    }

    public static boolean checkArguments(Map<String, String> args) {
        return Arrays.stream(REQUIRED_ARGS).filter(arg -> args.get(arg) == null).collect(Collectors.toList()).size() == 0;
    }

    public static String[] getRequiredArgs() {
        return REQUIRED_ARGS;
    }

    @Override
    public boolean isConnected() {
        try {
            return context().getJenkinsServer().isRunning();
        } catch (Exception e) {
            return false;
        }
    }
}
