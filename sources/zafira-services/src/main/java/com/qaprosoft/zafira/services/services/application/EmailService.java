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
package com.qaprosoft.zafira.services.services.application;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.services.services.application.integration.impl.MailSender;
import com.qaprosoft.zafira.services.services.application.emails.IEmailMessage;
import com.qaprosoft.zafira.services.util.FreemarkerUtil;

import javax.mail.MessagingException;

@Component
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final MailSender mailSender;
    private final FreemarkerUtil freemarkerUtil;
    private final EmailValidator validator;

    public EmailService(MailSender mailSender, FreemarkerUtil freemarkerUtil) {
        this.mailSender = mailSender;
        this.freemarkerUtil = freemarkerUtil;
        this.validator = EmailValidator.getInstance();
    }

    public String sendEmail(final IEmailMessage message, final String... emails) {

        if (!mailSender.isEnabledAndConnected()) {
            return null;
        }

        final String text = freemarkerUtil.getFreeMarkerTemplateContent(message.getType().getTemplateName(), message);
        final String[] recipients = processRecipients(emails);

        if (!ArrayUtils.isEmpty(recipients)) {
            final MimeMessagePreparator preparator = mimeMessage -> {
                boolean hasAttachments = message.getAttachments() != null;
                MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, hasAttachments);
                msg.setSubject(message.getSubject());
                msg.setTo(recipients);
                msgSetFrom(msg);
                msg.setText(text, true);
                if (hasAttachments) {
                    for (Attachment attachment : message.getAttachments()) {
                        msg.addAttachment(attachment.getName() + "." + FilenameUtils.getExtension(attachment.getFile().getName()),
                                attachment.getFile());
                        msg.addInline(attachment.getName().replaceAll(" ", "_"), attachment.getFile());
                    }
                }
            };
            this.mailSender.send(preparator);
        }
        return text;
    }

    private void msgSetFrom(MimeMessageHelper msg) throws UnsupportedEncodingException, MessagingException {
        JavaMailSenderImpl javaMailSender = mailSender.getJavaMailSenderImpl()
                .orElseThrow(() -> new ForbiddenOperationException("Unable to retrieve sender address"));
        String fromAddress = mailSender.getFromAddress()
                .orElseThrow(() -> new ForbiddenOperationException("Unable to retrieve sender address"));
        if (!StringUtils.isBlank(fromAddress)) {
            msg.setFrom(fromAddress, javaMailSender.getUsername());
        } else {
            msg.setFrom(javaMailSender.getUsername());
        }
    }

    private String[] processRecipients(String... emails) {
        return Arrays.stream(emails).filter(email -> {
            boolean isValid = validator.isValid(email);
            if (!isValid) {
                LOGGER.info("Not valid recipient specified: " + email);
            }
            return validator.isValid(email);
        }).toArray(String[]::new);
    }
}
