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
package com.qaprosoft.zafira.services.services.auth;

import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.auth.EmailType;
import com.qaprosoft.zafira.services.services.application.EmailService;
import com.qaprosoft.zafira.services.services.application.UserService;
import com.qaprosoft.zafira.services.services.application.emails.ForgotPasswordEmail;
import com.qaprosoft.zafira.services.services.application.emails.AbstractEmail;
import com.qaprosoft.zafira.services.services.application.emails.ForgotPasswordLdapEmail;
import com.qaprosoft.zafira.services.util.URLResolver;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ForgotPasswordService {

    private final String zafiraLogoURL;
    private final URLResolver urlResolver;
    private final EmailService emailService;
    private final UserService userService;

    public ForgotPasswordService(@Value("${zafira.slack.image}") String zafiraLogoURL,
            URLResolver urlResolver,
            EmailService emailService,
            UserService userService) {
        this.zafiraLogoURL = zafiraLogoURL;
        this.urlResolver = urlResolver;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendForgotPasswordEmail(EmailType emailType, User user) {
        AbstractEmail emailMessage;
        if (User.Source.INTERNAL.equals(user.getSource())) {
            String token = RandomStringUtils.randomAlphanumeric(50);
            userService.updateResetToken(token, user.getId());
            emailMessage = new ForgotPasswordEmail(token, zafiraLogoURL, urlResolver.buildWebURL());
        } else {
            emailMessage = new ForgotPasswordLdapEmail(zafiraLogoURL, urlResolver.buildWebURL());
        }
        emailService.sendEmail(emailMessage, emailType.getEmail());
    }

}
