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
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.models.dto.EmailType;
import com.qaprosoft.zafira.service.email.CommonEmail;
import com.qaprosoft.zafira.service.email.IEmailMessage;
import com.qaprosoft.zafira.service.integration.tool.impl.MailService;
import com.qaprosoft.zafira.service.util.EmailUtils;
import com.qaprosoft.zafira.service.util.FreemarkerUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private final MailService mailService;
    private final FreemarkerUtil freemarkerUtil;

    public EmailService(MailService mailService, FreemarkerUtil freemarkerUtil) {
        this.mailService = mailService;
        this.freemarkerUtil = freemarkerUtil;
    }

    public String sendEmail(final IEmailMessage message, final String... emails) {

        if (!mailService.isEnabledAndConnected(null)) {
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
                mailService.setFromAddress(msg);
                msg.setText(text, true);
                if (hasAttachments) {
                    for (Attachment attachment : message.getAttachments()) {
                        msg.addInline(attachment.getName().replaceAll(" ", "_"), attachment.getFile());
                    }
                }
            };
            this.mailService.send(preparator);
        }
        return text;
    }

    public String sendEmail(EmailType email, MultipartFile file) throws IOException {
        List<Attachment> attachments = new ArrayList<>();
        String filename = FilenameUtils.getName(file.getOriginalFilename());
        if (StringUtils.isEmpty(filename)) {
            filename = UUID.randomUUID().toString();
        }
        String fileExtension = String.format(".%s", FilenameUtils.getExtension(file.getOriginalFilename()));
        File attachment = File.createTempFile(filename, fileExtension);
        file.transferTo(attachment);
        attachments.add(new Attachment(email.getSubject(), attachment));
        String[] emails = EmailUtils.obtainRecipients(email.getRecipients());
        IEmailMessage message = new CommonEmail(email.getSubject(), email.getText(), attachments);
        return sendEmail(message, emails);
    }

    private String[] processRecipients(String... emails) {
        return Arrays.stream(emails).filter(email -> {
            boolean isValid = isValid(email);
            if (!isValid) {
                LOGGER.info("Not valid recipient specified: " + email);
            }
            return isValid;
        }).toArray(String[]::new);
    }

    private boolean isValid(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
