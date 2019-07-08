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
package com.qaprosoft.zafira.services.services.application.integration.impl;

import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import com.qaprosoft.zafira.services.services.application.integration.context.JiraContext;
import net.rcarz.jiraclient.Issue;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.qaprosoft.zafira.models.db.Setting.SettingType.JIRA_CLOSED_STATUS;
import static com.qaprosoft.zafira.models.db.Setting.Tool.JIRA;

@Component
public class JiraService extends AbstractIntegration<JiraContext> {

    private final SettingsService settingsService;

    public JiraService(SettingsService settingsService, CryptoService cryptoService) {
        super(settingsService, cryptoService, JIRA, JiraContext.class);
        this.settingsService = settingsService;
    }

    @Override
    public boolean isConnected() {
        try {
            return context().getJiraClient().getProjects() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<Issue> getIssue(String ticket) {
        return mapContext(context -> {
            Issue issue = null;
            try {
                issue = context.getJiraClient().getIssue(ticket);
            } catch (Exception e) {
                LOGGER.error("Unable to find Jira issue: " + ticket, e);
            }
            return issue;
        });
    }

    public boolean isIssueClosed(Issue issue) {
        boolean isIssueClosed = false;
        String[] closeStatuses = settingsService.getSettingValue(JIRA_CLOSED_STATUS).split(";");
        for (String closeStatus : closeStatuses) {
            if (issue.getStatus().getName().equalsIgnoreCase(closeStatus)) {
                isIssueClosed = true;
            }
        }
        return isIssueClosed;
    }

}
