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
package com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.automationserver;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.ExtractHeader;
import com.offbytwo.jenkins.model.FolderJob;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;
import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.models.dto.BuildParameterType;
import com.qaprosoft.zafira.models.dto.JobResult;
import com.qaprosoft.zafira.services.exceptions.ExternalSystemException;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.AdapterParam;
import com.qaprosoft.zafira.services.util.JenkinsClient;
import com.qaprosoft.zafira.services.util.JenkinsConfig;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

import static com.qaprosoft.zafira.models.dto.BuildParameterType.BuildParameterClass.BOOLEAN;
import static com.qaprosoft.zafira.models.dto.BuildParameterType.BuildParameterClass.HIDDEN;
import static com.qaprosoft.zafira.models.dto.BuildParameterType.BuildParameterClass.STRING;

public class JenkinsIntegrationAdapter extends AbstractIntegrationAdapter implements AutomationServerAdapter {

    private static final String LAUNCHER_JOB_URL_PATTERN = "%s/job/%s/job/launcher";
    private static final String LAUNCHER_JOB_ROOT_URL_PATTERN = "%s/job/launcher";

    private static final String SCANNER_JOB_URL_PATTERN = "%s/job/%s/job/RegisterRepository";
    private static final String SCANNER_JOB_ROOT_URL_PATTERN = "%s/job/RegisterRepository";
    private static final String RESCANNER_JOB_URL_PATTERN = "%s/job/%s/job/%s/job/onPush-%s";
    private static final String RESCANNER_JOB_ROOT_URL_PATTERN = "%s/job/%s/job/onPush-%s";
    private static final String FOLDER_REGEX = ".+job/.+/job.+";

    private static final Integer HTTP_TIMEOUT = 15;

    private final String url;
    private final String username;
    private final String tokenOrPassword;
    private final String folder;

    private JenkinsServer jenkinsServer;

    public JenkinsIntegrationAdapter(Integration integration) {
        super(integration);

        this.url = getAttributeValue(JenkinsParam.JENKINS_URL);
        this.username = getAttributeValue(JenkinsParam.JENKINS_USERNAME);
        this.tokenOrPassword = getAttributeValue(JenkinsParam.JENKINS_API_TOKEN_OR_PASSWORD);
        this.folder = getAttributeValue(JenkinsParam.JENKINS_FOLDER);

        try {
            JenkinsConfig config = new JenkinsConfig(username, tokenOrPassword, HTTP_TIMEOUT);
            this.jenkinsServer = new JenkinsServer(new JenkinsClient(new URI(url), config));
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private enum JenkinsParam implements AdapterParam {
        JENKINS_URL("JENKINS_URL"),
        JENKINS_USERNAME("JENKINS_USER"),
        JENKINS_API_TOKEN_OR_PASSWORD("JENKINS_API_TOKEN_OR_PASSWORD"),
        JENKINS_FOLDER("JENKINS_FOLDER");

        private final String name;

        JenkinsParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public JobResult buildJob(Job job, Map<String, String> jobParameters) {
        JobWithDetails ciJob = getJobWithDetails(job);
        return buildJob(ciJob, jobParameters);
    }

    @Override
    public JobResult buildJob(String jobUrl, Map<String, String> jobParameters) {
        JobWithDetails job = getJobByURL(jobUrl);
        return buildJob(job, jobParameters);
    }

    @Override
    public JobResult abortJob(Job ciJob, Integer buildNumber) {
        JobWithDetails job = getJobWithDetails(ciJob);
        return abortJob(job, buildNumber);
    }

    @Override
    public JobResult abortJob(String jobUrl, Integer buildNumber) {
        JobWithDetails job = getJobByURL(jobUrl);
        return abortJob(job, buildNumber);
    }

    @Override
    public JobResult buildScannerJob(String repositoryName, Map<String, String> jobParameters, boolean rescan) {
        return null;
    }

    @Override
    public JobResult abortScannerJob(String repositoryName, Integer buildNumber, boolean rescan) {
        return null;
    }

    @Override
    public String buildLauncherJobUrl() {
        return folder == null || folder.isBlank() ?
                String.format(LAUNCHER_JOB_ROOT_URL_PATTERN, url) :
                String.format(LAUNCHER_JOB_URL_PATTERN, url, folder);
    }

    @Override
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

    @Override
    public Optional<Map<String, String>> getBuildParametersMap(Job ciJob, Integer buildNumber) {
        Map<String, String> jobParameters = null;
        try {
            JobWithDetails job = getJobWithDetails(ciJob);
            jobParameters = job.getBuildByNumber(buildNumber).details().getParameters();
            jobParameters.put("ci_run_id", UUID.randomUUID().toString());
        } catch (Exception e) {
            LOGGER.error("Unable to get job:  " + e.getMessage());
        }
        return Optional.ofNullable(jobParameters);
    }

    @Override
    public Map<Integer, String> getBuildConsoleOutputHtml(Job ciJob, Integer buildNumber, Integer stringsCount, Integer fullCount) {
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

    @Override
    public Integer getBuildNumber(String queueItemUrl) {
        QueueReference queueReference = new QueueReference(queueItemUrl);
        Integer buildNumber = null;
        int attempts = 5;
        long millisToSleep = 2000;
        try {
            QueueItem queueItem = null;
            while(attempts > 0) {
                queueItem = jenkinsServer.getQueueItem(queueReference);
                if(queueItem.getExecutable() != null) {
                    break;
                }
                sleep(millisToSleep);
                attempts --;
            }
            Build build = jenkinsServer.getBuild(queueItem);
            buildNumber = build.getNumber();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return buildNumber;
    }

    @Override
    public Job getJobByUrl(String jobUrl) {
        Job job = null;
        JobWithDetails jobWithDetails = getJobByURL(jobUrl);
        if (jobWithDetails != null && jobWithDetails.getUrl() != null) {
            job = new Job(jobWithDetails.getDisplayName(), jobWithDetails.getUrl().replaceAll("/$", ""));
        }
        return job;
    }

    @Override
    public boolean checkArguments(Map<String, String> args) {
        return false;
    }

    @Override
    public String buildJobUrl(String repositoryName, boolean rescan) {
        String jobUrl;
        if(rescan) {
            jobUrl = formatReScannerJobUrl(folder, url, repositoryName);
        } else {
            jobUrl = formatScannerJobUrl(folder, url);
        }
        LOGGER.error("Jenkins job url: " + jobUrl);
        return jobUrl;
    }

    private JobResult buildJob(JobWithDetails job, Map<String, String> jobParameters) {
        JobResult result = null;
        try {
            QueueReference reference = job.build(jobParameters, true);
            boolean success = checkReference(reference);
            result = new JobResult(reference.getQueueItemUrlPart(), success);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

    private boolean checkReference(QueueReference reference) {
        return reference != null && !StringUtils.isEmpty(reference.getQueueItemUrlPart());
    }

    private JobWithDetails getJobWithDetails(Job ciJob) {
        return ciJob.getJobURL().matches(FOLDER_REGEX) ? getJobByURL(ciJob.getJobURL()) : getJobByName(ciJob.getName());
    }

    private JobWithDetails getJobByURL(String jobUrl) {
        JobWithDetails job;
        String folderUrl = jobUrl.substring(0, jobUrl.lastIndexOf("job/"));
        Path path = Paths.get(jobUrl);
        String jobName = path.getName(path.getNameCount() - 1).toString();
        String folderName = path.getName(path.getNameCount() - 3).toString();
        FolderJob folderJob = new FolderJob(folderName, folderUrl);
        try {
            job = jenkinsServer.getJob(folderJob, jobName);
        } catch (IOException e) {
            throw new ExternalSystemException(e.getMessage(), e);
        }
        return job;
    }

    private JobWithDetails getJobByName(String jobName) {
        JobWithDetails job;
        try {
            job = jenkinsServer.getJob(jobName);
        } catch (IOException e) {
            throw new ExternalSystemException(e.getMessage(), e);
        }
        return job;
    }

    private String formatReScannerJobUrl(String jenkinsFolder, String jenkinsHost, String repositoryName){
        String reScannerJobUrl;
        if (StringUtils.isEmpty(jenkinsFolder)) {
            reScannerJobUrl = String.format(RESCANNER_JOB_ROOT_URL_PATTERN, jenkinsHost, repositoryName, repositoryName);
        } else {
            reScannerJobUrl = String.format(RESCANNER_JOB_URL_PATTERN, jenkinsHost, jenkinsFolder, repositoryName, repositoryName);
        }
        return reScannerJobUrl;
    }

    private String formatScannerJobUrl(String jenkinsFolder, String jenkinsHost){
        String scannerJobUrl;
        if (StringUtils.isEmpty(jenkinsFolder)) {
            scannerJobUrl = String.format(SCANNER_JOB_ROOT_URL_PATTERN, jenkinsHost);
        } else {
            scannerJobUrl = String.format(SCANNER_JOB_URL_PATTERN, jenkinsHost, jenkinsFolder);
        }
        return scannerJobUrl;
    }

    private JobResult abortJob(JobWithDetails job, Integer buildNumber) {
        JobResult result = null;
        try {
            QueueReference reference = stop(job, buildNumber);
            boolean success = checkReference(reference);
            if (!success) {
                reference = terminate(job, buildNumber);
                success = checkReference(reference);
            }
            result = new JobResult(reference.getQueueItemUrlPart(), success);
        } catch (Exception e) {
            LOGGER.error("Unable to abort Jenkins job:  " + e.getMessage());
        }
        return result;
    }

    private QueueReference stop(JobWithDetails job, Integer buildNumber) throws IOException {
        ExtractHeader location = job.getClient().post(job.getUrl() + buildNumber + "/stop",
                null, ExtractHeader.class);
        return new QueueReference(location.getLocation());
    }

    private QueueReference terminate(JobWithDetails job, Integer buildNumber) throws IOException {
        ExtractHeader location = job.getClient().post(job.getUrl() + buildNumber + "/term",
                null, ExtractHeader.class);
        return new QueueReference(location.getLocation());
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

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return jenkinsServer.isRunning();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getTokenOrPassword() {
        return tokenOrPassword;
    }

    @Override
    public String getFolder() {
        return folder;
    }

    public JenkinsServer getJenkinsServer() {
        return jenkinsServer;
    }
}
