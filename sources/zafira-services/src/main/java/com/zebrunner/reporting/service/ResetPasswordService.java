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
package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.service.email.AbstractEmail;
import com.zebrunner.reporting.service.email.ResetPasswordEmail;
import com.zebrunner.reporting.service.email.ResetPasswordLdapEmail;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.util.URLResolver;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.CREDENTIALS_RESET_IS_NOT_POSSIBLE;

@Service
public class ResetPasswordService {

    private static final String USER_FOR_PASSWORD_RESET_IS_NOT_FOUND = "User for password reset is not found";

    private final String zafiraLogoURL;
    private final URLResolver urlResolver;
    private final EmailService emailService;
    private final UserService userService;

    public ResetPasswordService(
            @Value("${zafira.slack.image-url}") String zafiraLogoURL,
            URLResolver urlResolver,
            EmailService emailService,
            UserService userService
    ) {
        this.zafiraLogoURL = zafiraLogoURL;
        this.urlResolver = urlResolver;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendResetPasswordEmail(String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new IllegalOperationException(CREDENTIALS_RESET_IS_NOT_POSSIBLE, USER_FOR_PASSWORD_RESET_IS_NOT_FOUND);
        }
        AbstractEmail emailMessage;
        if (User.Source.INTERNAL.equals(user.getSource())) {
            String token = RandomStringUtils.randomAlphanumeric(50);
            userService.updateResetToken(token, user.getId());
            emailMessage = new ResetPasswordEmail(token, zafiraLogoURL, urlResolver.buildWebURL());
        } else {
            emailMessage = new ResetPasswordLdapEmail(zafiraLogoURL, urlResolver.buildWebURL());
        }
        emailService.sendEmail(emailMessage, email);
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String token, String password) {
        User user = userService.getUserByResetToken(token);
        userService.updateUserPassword(user, password);
        userService.updateResetToken(null, user.getId());
    }
}
