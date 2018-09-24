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
package com.qaprosoft.zafira.services.services.application.emails.password;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.services.services.application.emails.EmailType;
import com.qaprosoft.zafira.services.services.application.emails.AbstractEmail;

import java.util.List;

public class ForgotPasswordEmail extends AbstractEmail {

    private static final String SUBJECT = "Forgot password in Zafira";

    private String token;

    public ForgotPasswordEmail(String token, String zafiraLogoURL, String companyLogoURL, String workspaceURL) {
        super(SUBJECT, zafiraLogoURL, companyLogoURL, workspaceURL);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
        return EmailType.FORGOT_PASSWORD;
    }

    @Override
    public List<Attachment> getAttachments() {
        return null;
    }
}
