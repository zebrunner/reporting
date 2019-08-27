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
package com.qaprosoft.zafira.services.services.application.integration.tool.impl;

import com.qaprosoft.zafira.services.services.application.integration.tool.AbstractIntegration;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.mail.MailServiceAdapter;
import org.apache.commons.lang.StringUtils;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

@Component
public class MailService extends AbstractIntegration<MailServiceAdapter> {

    public MailService() {
        super("EMAIL");
    }

    public CompletableFuture<Void> send(MimeMessagePreparator preparator) {
        MailServiceAdapter mailServiceAdapter = getDefaultAdapter();
        return mailServiceAdapter.send(preparator);
    }

    public void setFromAddress(MimeMessageHelper msg) throws MessagingException, UnsupportedEncodingException {
        MailServiceAdapter mailServiceAdapter = getDefaultAdapter();
        String fromAddress = mailServiceAdapter.getFromAddress();
        String username = mailServiceAdapter.getUsername();
        if (!StringUtils.isBlank(fromAddress)) {
            msg.setFrom(fromAddress, username);
        } else {
            msg.setFrom(username);
        }
    }

}
