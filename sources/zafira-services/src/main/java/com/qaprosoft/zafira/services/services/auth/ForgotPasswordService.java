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
package com.qaprosoft.zafira.services.services.auth;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.auth.EmailType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.EmailService;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.UserService;
import com.qaprosoft.zafira.services.services.application.emails.password.ForgotPasswordEmail;
import com.qaprosoft.zafira.services.services.application.emails.AbstractEmail;
import com.qaprosoft.zafira.services.services.application.emails.password.ForgotPasswordLdapEmail;
import com.qaprosoft.zafira.services.util.URLResolver;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ForgotPasswordService {

    @Value("${zafira.slack.image}")
    private String zafiraLogoURL;

    @Autowired
    private URLResolver urlResolver;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private UserService userService;

    @Transactional(rollbackFor = Exception.class)
    public void sendForgotPasswordEmail(EmailType emailType, User user) throws ServiceException {
        AbstractEmail emailMessage = null;
        if(user.getSource().equals(User.Source.INTERNAL)) {
            String token = RandomStringUtils.randomAlphanumeric(50);
            userService.updateResetToken(token, user.getId());
            emailMessage = new ForgotPasswordEmail(token, zafiraLogoURL, settingsService.getSettingValue(Setting.SettingType.COMPANY_LOGO_URL), urlResolver.buildWebURL());
        } else {
            emailMessage = new ForgotPasswordLdapEmail(zafiraLogoURL, settingsService.getSettingValue(Setting.SettingType.COMPANY_LOGO_URL), urlResolver.buildWebURL());
        }
        emailService.sendEmail(emailMessage, emailType.getEmail());
    }
}
