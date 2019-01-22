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
 *******************************************************************************/
package com.qaprosoft.zafira.services.services.application.emails;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.jmx.CryptoService;
import com.qaprosoft.zafira.services.services.application.jmx.IJMXService;
import com.qaprosoft.zafira.services.services.application.jmx.context.EmailContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.MessagingException;
import java.util.List;

import static com.qaprosoft.zafira.models.db.Setting.Tool.EMAIL;

public class AsynSendEmailTask implements Runnable, IJMXService<EmailContext> {

    private static final Logger LOGGER = Logger.getLogger(AsynSendEmailTask.class);

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CryptoService cryptoService;

    private MimeMessagePreparator preparator;

    @Override
    public void run() {
        getJavaMailSenderImpl().send(preparator);
    }

    @Override
    public void init() {
        String host = null;
        int port = 0;
        String user = null;
        String fromAddress = null;
        String password = null;
        try {
            List<Setting> emailSettings = settingsService.getSettingsByTool(EMAIL);
            for (Setting setting : emailSettings) {
                if (setting.isEncrypted()) {
                    setting.setValue(cryptoService.decrypt(setting.getValue()));
                }
                switch (Setting.SettingType.valueOf(setting.getName())) {
                case EMAIL_HOST:
                    host = setting.getValue();
                    break;
                case EMAIL_PORT:
                    port = StringUtils.isBlank(setting.getValue()) ? 0 : Integer.valueOf(setting.getValue());
                    break;
                case EMAIL_USER:
                    user = setting.getValue();
                    break;
                case EMAIL_FROM_ADDRESS:
                    fromAddress = setting.getValue();
                    break;
                case EMAIL_PASSWORD:
                    password = setting.getValue();
                    break;
                default:
                    break;
                }
            }
            init(host, port, user, fromAddress, password);
        } catch (Exception e) {
            LOGGER.error("Setting does not exist", e);
        }
    }

    public void init(String host, int port, String user, String fromAddress, String password) {
        try {
            if (!StringUtils.isBlank(host) && port != 0) {
                putContext(EMAIL, new EmailContext(host, port, user, fromAddress, password));
            }
        } catch (Exception e) {
            LOGGER.error("Unable to initialize SMTP integration: " + e.getMessage());
        }
    }

    @Override
    public boolean isConnected() {
        boolean connected = false;
        JavaMailSenderImpl sender = getJavaMailSenderImpl();
        if (sender != null) {
            try {
                sender.testConnection();
                connected = true;
            } catch (MessagingException e) {
                // Will be thrown when SMTP not configured properly
            }
        } 
        return connected;
    }

    public void setPreparator(MimeMessagePreparator preparator) {
        this.preparator = preparator;
    }

    public JavaMailSenderImpl getJavaMailSenderImpl() {
        return getContext(EMAIL) != null ? (JavaMailSenderImpl) getContext(EMAIL).getJavaMailSender() : null;
    }

    public String getFromAddress() {
        return getContext(EMAIL) != null ? getContext(EMAIL).getFromAddress() : null;
    }
}
