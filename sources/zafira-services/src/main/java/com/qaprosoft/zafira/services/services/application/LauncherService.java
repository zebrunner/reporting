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
package com.qaprosoft.zafira.services.services.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.qaprosoft.zafira.models.dto.JobResult;
import com.qaprosoft.zafira.models.dto.ScannedRepoLaunchersType;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.services.application.scm.GitHubService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.LauncherMapper;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.db.Launcher;
import com.qaprosoft.zafira.models.db.ScmAccount;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.services.exceptions.ScmAccountNotFoundException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.integration.context.JenkinsContext;
import com.qaprosoft.zafira.services.services.application.integration.impl.JenkinsService;
import com.qaprosoft.zafira.services.services.application.scm.ScmAccountService;
import com.qaprosoft.zafira.services.services.auth.JWTService;

@Service
public class LauncherService {

    private final LauncherMapper launcherMapper;
    private final JenkinsService jenkinsService;
    private final ScmAccountService scmAccountService;
    private final JobsService jobsService;
    private final JWTService jwtService;
    private final GitHubService gitHubService;
    private final String apiUrl;

    public LauncherService(LauncherMapper launcherMapper,
                           JenkinsService jenkinsService,
                           ScmAccountService scmAccountService,
                           JobsService jobsService,
                           JWTService jwtService,
                           GitHubService gitHubService,
                           @Value("${zafira.webservice.url}") String apiUrl) {
        this.launcherMapper = launcherMapper;
        this.jenkinsService = jenkinsService;
        this.scmAccountService = scmAccountService;
        this.jobsService = jobsService;
        this.jwtService = jwtService;
        this.gitHubService = gitHubService;
        this.apiUrl = apiUrl;
    }

    @Transactional(rollbackFor = Exception.class)
    public Launcher createLauncher(Launcher launcher, User owner) throws ServiceException {
        if (jenkinsService.isConnected()) {
            JenkinsContext context = jenkinsService.context();
            String launcherJobName = context.getLauncherJobName();
            if (launcherJobName != null) {
                String jenkinsHost = context.getJenkinsHost();
                String launcherJobUrl = Arrays.stream(launcherJobName.split("/")).collect(Collectors.joining("/job/", jenkinsHost + "/job/", ""));
                Job job = jobsService.getJobByJobURL(launcherJobUrl);
                if (job == null) {
                    job = jenkinsService.getJobByUrl(launcherJobUrl).orElse(null);
                    if (job != null) {
                        job.setJenkinsHost(jenkinsHost);
                        job.setUser(owner);
                        jobsService.createJob(job);
                    }
                }
                launcher.setJob(job);
            }
        }
        launcherMapper.createLauncher(launcher);
        return launcher;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Launcher> createLaunchersForJob(ScannedRepoLaunchersType scannedRepoLaunchersType, User owner) throws ServiceException {
        if (!scannedRepoLaunchersType.isSuccess()) {
            return new ArrayList<>();
        }
        ScmAccount scmAccount = scmAccountService.getScmAccountByRepo(scannedRepoLaunchersType.getRepo());
        if (scmAccount == null)
            throw new ScmAccountNotFoundException("Unable to find scm account for repo");

        deleteAutoScannedLaunchersByScmAccountId(scmAccount.getId());

        List<Launcher> result = scannedRepoLaunchersType.getJenkinsLaunchers().stream().map(jenkinsLauncherType -> {
            String jobUrl = jenkinsLauncherType.getJobUrl();
            Job job = jobsService.getJobByJobURL(jobUrl);
            if (job == null) {
                job = jobsService.createOrUpdateJobByURL(jobUrl, owner);
            }
            Launcher launcher = new Launcher(job.getName(), jenkinsLauncherType.getJobParameters(), scmAccount, job, true);
            launcherMapper.createLauncher(launcher);
            return launcher;
        }).collect(Collectors.toList());
        return result;
    }

    @Transactional(readOnly = true)
    public Launcher getLauncherById(Long id) throws ServiceException {
        return launcherMapper.getLauncherById(id);
    }

    @Transactional(readOnly = true)
    public Launcher getLauncherByJobId(Long id) throws ServiceException {
        return launcherMapper.getLauncherByJobId(id);
    }

    @Transactional(readOnly = true)
    public List<Launcher> getAllLaunchers() throws ServiceException {
        return launcherMapper.getAllLaunchers();
    }

    @Transactional(rollbackFor = Exception.class)
    public Launcher updateLauncher(Launcher launcher) throws ServiceException {
        launcherMapper.updateLauncher(launcher);
        return launcher;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteLauncherById(Long id) throws ServiceException {
        launcherMapper.deleteLauncherById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAutoScannedLaunchersByScmAccountId(Long scmAccountId) throws ServiceException {
        launcherMapper.deleteAutoScannedLaunchersByScmAccountId(scmAccountId);
    }

    @Transactional(readOnly = true)
    public void buildLauncherJob(Launcher launcher, User user) throws IOException, ServiceException {

        ScmAccount scmAccount = scmAccountService.getScmAccountById(launcher.getScmAccount().getId());
        if (scmAccount == null)
            throw new ServiceException("Scm account not found");

        Job job = launcher.getJob();
        if (job == null)
            throw new ServiceException("Launcher job not specified");

        Map<String, String> jobParameters = new ObjectMapper().readValue(launcher.getModel(), new TypeReference<Map<String, String>>() {});
        jobParameters.put("scmURL", scmAccount.buildAuthorizedURL());
        if (!jobParameters.containsKey("branch")) {
            jobParameters.put("branch", "*/master");
        }

        jobParameters.put("zafira_enabled", "true");
        jobParameters.put("zafira_service_url", apiUrl.replace("api", TenancyContext.getTenantName()));
        jobParameters.put("zafira_access_token", jwtService.generateAccessToken(user, TenancyContext.getTenantName()));

        String args = jobParameters.entrySet().stream().filter(param -> !Arrays.asList(JenkinsService.getRequiredArgs()).contains(param.getKey()))
                .map(param -> param.getKey() + "=" + param.getValue()).collect(Collectors.joining(","));

        jobParameters.put("overrideFields", args);

        if (!JenkinsService.checkArguments(jobParameters))
            throw new ServiceException("Required arguments not found");

        jenkinsService.buildJob(job, jobParameters);
    }

    @Transactional(readOnly = true)
    public JobResult buildScannerJob(String tenantName, Long userId, String branch, long scmAccountId, boolean rescan) {
        JobResult result;
        ScmAccount scmAccount = scmAccountService.getScmAccountById(scmAccountId);
        if(scmAccount == null) {
            throw new ServiceException("Scm account not found");
        }
        String loginName = gitHubService.getLoginName(scmAccount.getAccessToken());

        Map<String, String> jobParameters = new HashMap<>();
        jobParameters.put("userId", String.valueOf(userId));
        jobParameters.put("organization", scmAccount.getOrganizationName());
        jobParameters.put("repo", scmAccount.getRepositoryName());
        jobParameters.put("branch", branch);
        jobParameters.put("githubUser", loginName);
        jobParameters.put("githubToken", scmAccount.getAccessToken());

        result = jenkinsService.buildScannerJob(tenantName, scmAccount.getRepositoryName(), jobParameters, rescan);
        if (!result.isSuccess()) {
            throw new ForbiddenOperationException("Repository scanner job is not started");
        }
        return result;
    }

    public JobResult abortScannerJob(String tenantName, long scmAccountId, boolean rescan, Integer buildNumber) {
        ScmAccount scmAccount = scmAccountService.getScmAccountById(scmAccountId);
        if(scmAccount == null) {
            throw new ServiceException("Scm account not found");
        }

        JobResult result = jenkinsService.abortScannerJob(tenantName, scmAccount.getRepositoryName(), rescan, buildNumber);
        if (result == null || !result.isSuccess()) {
            throw new ForbiddenOperationException("Repository scanner job is not aborted");
        }
        return result;
    }

}
