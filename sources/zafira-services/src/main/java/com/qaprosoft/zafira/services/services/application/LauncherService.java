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

import com.qaprosoft.zafira.models.db.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.LauncherMapper;
import com.qaprosoft.zafira.models.db.Launcher;
import com.qaprosoft.zafira.models.db.ScmAccount;
import com.qaprosoft.zafira.models.dto.JenkinsJobType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.jmx.JenkinsService;
import com.qaprosoft.zafira.services.services.application.scm.ScmAccountService;

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

    @Transactional(rollbackFor = Exception.class)
    public Launcher createLauncher(Launcher launcher) throws ServiceException {
        if(jenkinsService.getContext() != null) {
            String launcherJobName = jenkinsService.getContext().getLauncherJobName();
            if (launcherJobName != null) {
                Job job = jobsService.getJobByName(launcherJobName);
                if(job == null) {
                    job = jenkinsService.getJob(launcherJobName);
                    if (job != null) {
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

    public void buildLauncherJob(Launcher launcher) throws IOException, ServiceException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> args = mapper.readValue(launcher.getModel(), new TypeReference<Map<String, String>>(){});
        if(! JenkinsService.checkArguments(args)) {
            throw new ServiceException("Required arguments not found");
        }
        ScmAccount scmAccount = scmAccountService.getScmAccountById(launcher.getScmAccount().getId());
        if(scmAccount == null) {
            throw new ServiceException("Scm account not found");
        }
        JenkinsJobType jenkinsJobType = new JenkinsJobType(
                args.get("suite"),
                JenkinsService.buildURL(scmAccount.getRepositoryURL(), scmAccount.getAccessToken()),
                scmAccount.getId(),
                args.get("branch"));
        Arrays.stream(JenkinsService.getRequiredArgs()).forEach(args::remove);
        jenkinsJobType.setArgs(args);
        jenkinsService.build(jenkinsJobType);
    }
}
