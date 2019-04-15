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

import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import com.qaprosoft.zafira.services.services.application.integration.context.EmailContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.qaprosoft.zafira.models.db.Setting.Tool.EMAIL;

@Component
public class MailSender extends AbstractIntegration<EmailContext> {

    public MailSender(SettingsService settingsService, CryptoService cryptoService) {
        super(settingsService, cryptoService, EMAIL, EmailContext.class);
    }

    public CompletableFuture<Void> send(MimeMessagePreparator preparator) {
        return CompletableFuture.runAsync(() -> getJavaMailSenderImpl()
                .orElseThrow(() -> new ForbiddenOperationException("Mail sender is not configured properly"))
                .send(preparator));
    }

    @Override
    public boolean isConnected() {
        return obtainConnectedStatus().orElseGet(() -> {
            boolean connected = false;
            Optional<JavaMailSenderImpl> maybeSender = getJavaMailSenderImpl();
            if (maybeSender.isPresent()) {
                try {
                    maybeSender.get().testConnection();
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

    /**
     *
     * @return
     */
    private Optional<Boolean> obtainConnectedStatus() {
        return mapContext(EmailContext::isConnected);
    }

}
