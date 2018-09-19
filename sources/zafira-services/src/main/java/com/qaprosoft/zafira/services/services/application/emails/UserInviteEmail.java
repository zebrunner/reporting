/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.services.services.application.emails;

import com.qaprosoft.zafira.models.db.Attachment;

import java.util.List;

public class UserInviteEmail implements IEmailMessage {

    private static final String SUBJECT = "Invitation to Zafira";

    private String token;
    private String zafiraLogoURL;
    private String companyLogoURL;
    private String workspaceURL;

    public UserInviteEmail(String token, String zafiraLogoURL, String companyLogoURL, String workspaceURL) {
        this.token = token;
        this.zafiraLogoURL = zafiraLogoURL;
        this.companyLogoURL = companyLogoURL;
        this.workspaceURL = workspaceURL;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getZafiraLogoURL() {
        return zafiraLogoURL;
    }

    public void setZafiraLogoURL(String zafiraLogoURL) {
        this.zafiraLogoURL = zafiraLogoURL;
    }

    public String getCompanyLogoURL() {
        return companyLogoURL;
    }

    public void setCompanyLogoURL(String companyLogoURL) {
        this.companyLogoURL = companyLogoURL;
    }

    public String getWorkspaceURL() {
        return workspaceURL;
    }

    public void setWorkspaceURL(String workspaceURL) {
        this.workspaceURL = workspaceURL;
    }

    @Override
    public String getSubject() {
        return SUBJECT;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public EmailType getType() {
        return EmailType.USER_INVITE;
    }

    @Override
    public List<Attachment> getAttachments() {
        return null;
    }
}
