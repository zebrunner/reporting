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
 ******************************************************************************/
package com.qaprosoft.zafira.service.integration.tool.adapter.mail;

import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.service.integration.tool.adapter.AdapterParam;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.MessagingException;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class MailIntegrationAdapter extends AbstractIntegrationAdapter implements MailServiceAdapter {

    private static final int SMTP_NOT_SECURED_PORT = 25;

    private static final String ERR_MSG_SMTP_CONNECTION_IS_NOT_ESTABLISHED = "SMTP connection is not established";

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String fromAddress;

    private final JavaMailSender javaMailSender;
    private Boolean isConnected;

    public MailIntegrationAdapter(Integration integration) {
        super(integration);

        this.host = getAttributeValue(integration, EmailParam.EMAIL_HOST);
        this.port = Integer.parseInt(getAttributeValue(integration, EmailParam.EMAIL_PORT));
        this.username = getAttributeValue(integration, EmailParam.EMAIL_USERNAME);
        this.password = getAttributeValue(integration, EmailParam.EMAIL_PASSWORD);
        this.fromAddress = getAttributeValue(integration, EmailParam.EMAIL_FROM_ADDRESS);

        boolean enableTls = this.port != SMTP_NOT_SECURED_PORT;

        this.javaMailSender = new JavaMailSenderImpl();
        ((JavaMailSenderImpl) this.javaMailSender).setDefaultEncoding("UTF-8");
        ((JavaMailSenderImpl) this.javaMailSender).setJavaMailProperties(new Properties() {
            private static final long serialVersionUID = -7384945982042097581L;
            {
                setProperty("mail.smtp.auth", "true");
                setProperty("mail.smtp.starttls.enable", String.valueOf(enableTls));

                if (enableTls) {
                    setProperty("mail.transport.protocol", "smtp");
                    setProperty("mail.smtps.ssl.protocols", "TLSv1.2");
                }

                setProperty("mail.smtp.connectiontimeout", "5000");
                setProperty("mail.smtp.timeout", "3000");
            }
        });
        ((JavaMailSenderImpl) this.javaMailSender).setHost(host);
        ((JavaMailSenderImpl) this.javaMailSender).setPort(port);
        ((JavaMailSenderImpl) this.javaMailSender).setUsername(username);
        ((JavaMailSenderImpl) this.javaMailSender).setPassword(password);
    }

    private enum EmailParam implements AdapterParam {
        EMAIL_HOST("EMAIL_HOST"),
        EMAIL_PORT("EMAIL_PORT"),
        EMAIL_USERNAME("EMAIL_USER"),
        EMAIL_PASSWORD("EMAIL_PASSWORD"),
        EMAIL_FROM_ADDRESS("EMAIL_FROM_ADDRESS");

        private final String name;

        EmailParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public boolean isConnected() {
        return obtainConnectedStatus().orElseGet(() -> {
            boolean connected;
            JavaMailSenderImpl sender = (JavaMailSenderImpl) javaMailSender;
            try {
                sender.testConnection();
                connected = true;
            } catch (MessagingException e) {
                // Will be thrown when SMTP not configured properly
                LOGGER.error(ERR_MSG_SMTP_CONNECTION_IS_NOT_ESTABLISHED, e);
                connected = false;
            }
            setConnected(connected);
            return connected;
        });
    }

    @Override
    public CompletableFuture<Void> send(MimeMessagePreparator preparator) {
        return CompletableFuture.runAsync(() -> javaMailSender.send(preparator));
    }

    @Override
    public String getFromAddress() {
        return fromAddress;
    }

    @Override
    public String getUsername() {
        return username;
    }

    private Optional<Boolean> obtainConnectedStatus() {
        return Optional.ofNullable(isConnected);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public JavaMailSender getJavaMailSender() {
        return javaMailSender;
    }

    public void setConnected(Boolean connected) {
        isConnected = connected;
    }
}
