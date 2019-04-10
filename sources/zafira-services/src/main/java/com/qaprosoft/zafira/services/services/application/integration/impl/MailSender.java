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
package com.qaprosoft.zafira.services.services.application.integration.impl;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import com.qaprosoft.zafira.services.services.application.integration.context.EmailContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.qaprosoft.zafira.models.db.Setting.Tool.EMAIL;

@Component
public class MailSender extends AbstractIntegration<EmailContext> {

    private static final Logger LOGGER = Logger.getLogger(MailSender.class);

    private final SettingsService settingsService;
    private final CryptoService cryptoService;

    public MailSender(SettingsService settingsService, CryptoService cryptoService) {
        super(EMAIL);
        this.settingsService = settingsService;
        this.cryptoService = cryptoService;
    }

    @Override
    public void init() {
        String host = null;
        int port = 0;
        String user = null;
        String fromAddress = null;
        String password = null;
        boolean enabled = false;

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
                case EMAIL_ENABLED:
                    enabled = Boolean.valueOf(setting.getValue());
                    break;
                default:
                    break;
                }
            }
            init(host, port, user, fromAddress, password, enabled);
        } catch (Exception e) {
            LOGGER.error("Setting does not exist", e);
        }
    }

    public void init(String host, int port, String user, String fromAddress, String password, boolean enabled) {
        try {
            if (!StringUtils.isBlank(host) && !StringUtils.isBlank(user) && !StringUtils.isBlank(password) && port != 0) {
                putContext(new EmailContext(host, port, user, fromAddress, password, enabled));
            }
        } catch (Exception e) {
            LOGGER.error("Unable to initialize SMTP integration: " + e.getMessage());
        }
    }

    public CompletableFuture<Void> send(MimeMessagePreparator preparator) {
        return CompletableFuture.runAsync(() -> getJavaMailSenderImpl()
                .orElseThrow(() -> new ForbiddenOperationException("Mail sender is not configured properly"))
                .send(preparator));
    }

    @Override
    public boolean isConnected() {
        return isContextConnected().orElseGet(() -> {
            boolean connected = false;
            JavaMailSenderImpl sender = getJavaMailSenderImpl().orElse(null);
            if (sender != null) {
                try {
                    sender.testConnection();
                    connected = true;
                } catch (MessagingException e) {
                    // Will be thrown when SMTP not configured properly
                }
            }
            setConnected(connected);
            return connected;
        });
    }

    public Optional<JavaMailSenderImpl> getJavaMailSenderImpl() {
        return mapContext(context -> (JavaMailSenderImpl) context.getJavaMailSender());
    }

    public Optional<String> getFromAddress() {
        return mapContext(EmailContext::getFromAddress);
    }

    private void setConnected(boolean isConnected) {
        getContext().ifPresent(context -> context().setConnected(isConnected));
    }

    private Optional<Boolean> isContextConnected() {
        return mapContext(EmailContext::isConnected);
    }

}
