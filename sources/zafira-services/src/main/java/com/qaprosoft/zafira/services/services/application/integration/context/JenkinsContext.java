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
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.integration.context;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import com.offbytwo.jenkins.JenkinsServer;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.util.JenkinsClient;
import com.qaprosoft.zafira.services.util.JenkinsConfig;

public class JenkinsContext extends AbstractContext {

    private static final Integer HTTP_TIMEOUT = 15;

    private JenkinsServer jenkinsServer;
    private String launcherJobName;
    private String jenkinsHost;

    public JenkinsContext(Map<Setting.SettingType, String> settings) {
        super(settings, settings.get(Setting.SettingType.JENKINS_ENABLED));
        try {
            String username = settings.get(Setting.SettingType.JENKINS_USER);
            String passwordOrApiToken = settings.get(Setting.SettingType.JENKINS_API_TOKEN_OR_PASSWORD);
            String url = settings.get(Setting.SettingType.JENKINS_URL);
            String launcherJobName = settings.get(Setting.SettingType.JENKINS_LAUNCHER_JOB_NAME);

            JenkinsConfig config = new JenkinsConfig(username, passwordOrApiToken, HTTP_TIMEOUT);
            this.jenkinsServer = new JenkinsServer(new JenkinsClient(new URI(url), config));
            this.launcherJobName = launcherJobName;
            this.jenkinsHost = url;
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public JenkinsServer getJenkinsServer() {
        return jenkinsServer;
    }

    public void setJenkinsServer(JenkinsServer jenkinsServer) {
        this.jenkinsServer = jenkinsServer;
    }

    public String getLauncherJobName() {
        return launcherJobName;
    }

    public void setLauncherJobName(String launcherJobName) {
        this.launcherJobName = launcherJobName;
    }

    public String getJenkinsHost() {
        return jenkinsHost;
    }

    public void setJenkinsHost(String jenkinsHost) {
        this.jenkinsHost = jenkinsHost;
    }
}
