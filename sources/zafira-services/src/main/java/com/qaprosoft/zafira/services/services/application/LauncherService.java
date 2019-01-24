/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.jmx.JenkinsService;
import com.qaprosoft.zafira.services.services.application.scm.ScmAccountService;
import com.qaprosoft.zafira.services.services.auth.JWTService;

@Service
public class LauncherService {

    @Autowired
    private LauncherMapper launcherMapper;

    @Autowired
    private JenkinsService jenkinsService;

    @Autowired
    private ScmAccountService scmAccountService;

    @Autowired
    private JobsService jobsService;
    
    @Autowired
    private JWTService jwtService;
    
    @Value("${zafira.webservice.url}")
    private String apiURL;

    @Transactional(rollbackFor = Exception.class)
    public Launcher createLauncher(Launcher launcher, User owner) throws ServiceException {
        if(jenkinsService.getContext() != null) {
            String launcherJobName = jenkinsService.getContext().getLauncherJobName();
            if (launcherJobName != null) {
                Job job = jobsService.getJobByName(launcherJobName);
                if(job == null) {
                    job = jenkinsService.getJob(launcherJobName);
                    if (job != null) {
                        job.setJenkinsHost(jenkinsService.getContext().getJenkinsHost());
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

    @Transactional(readOnly = true)
    public Launcher getLauncherById(Long id) throws ServiceException {
        return launcherMapper.getLauncherById(id);
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

    public void buildLauncherJob(Launcher launcher, User user) throws IOException, ServiceException {
        
        ScmAccount scmAccount = scmAccountService.getScmAccountById(launcher.getScmAccount().getId());
        if(scmAccount == null) 
            throw new ServiceException("Scm account not found");
        
        Job job = launcher.getJob();
        if(job == null)  
            throw new ServiceException("Launcher job not specified");
        
        Map<String, String> jobParameters = new ObjectMapper().readValue(launcher.getModel(), new TypeReference<Map<String, String>>(){});
        jobParameters.put("scmURL", scmAccount.buildAuthorizedURL());
        if(!jobParameters.containsKey("scmBranch")) {
            jobParameters.put("scmBranch", "*/master");
        }
        
        jobParameters.put("zafira_enabled", "true");
        jobParameters.put("zafira_service_url", apiURL.replace("api", TenancyContext.getTenantName()));
        jobParameters.put("zafira_access_token", jwtService.generateAccessToken(user, TenancyContext.getTenantName()));
        
        String args = jobParameters.entrySet().stream().filter(param -> ! Arrays.asList(JenkinsService.getRequiredArgs()).contains(param.getKey()))
                .map(param -> "-D" + param.getKey() + "=" + param.getValue()).collect(Collectors.joining(" "));
        
        jobParameters.put("args", args);
        
        if(!JenkinsService.checkArguments(jobParameters)) 
            throw new ServiceException("Required arguments not found");
        
        jenkinsService.buildJob(job, jobParameters);
    }
}
