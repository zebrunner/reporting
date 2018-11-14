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
package com.qaprosoft.zafira.services.services.application;

import static com.qaprosoft.zafira.models.db.Setting.Tool.EMAIL;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.Executors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.emails.AsynSendEmailTask;
import com.qaprosoft.zafira.services.services.application.emails.IEmailMessage;
import com.qaprosoft.zafira.services.util.FreemarkerUtil;

import javax.mail.MessagingException;

@Service
public class EmailService
{
	private static final Logger LOGGER = Logger.getLogger(EmailService.class);

	@Autowired
	private FreemarkerUtil freemarkerUtil;

	/*@Autowired
	private AutowireCapableBeanFactory autowireizer;*/

	@Autowired
	private AsynSendEmailTask emailTask;

	@Autowired
	private SettingsService settingsService;
	
	private static final EmailValidator validator = EmailValidator.getInstance();

	@SuppressWarnings("SynchronizeOnNonFinalField")
	public String sendEmail(final IEmailMessage message, final String... emails) throws ServiceException
	{

		if(! settingsService.isConnected(EMAIL))
		{
			return null;
		}

		final String text = freemarkerUtil.getFreeMarkerTemplateContent(message.getType().getTemplateName(), message);

		synchronized (emailTask) {
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
							msg.addAttachment(attachment.getName() + "." + FilenameUtils.getExtension(attachment.getFile().getName()), attachment.getFile());
							msg.addInline(attachment.getName().replaceAll(" ", "_"), attachment.getFile());
						}
					}
				};
				this.emailTask.setPreparator(preparator);
				//autowireizer.autowireBean(this.emailTask);
				Executors.newSingleThreadExecutor().execute(this.emailTask);
			}
		}
		return text;
	}

	private void msgSetFrom(MimeMessageHelper msg) throws UnsupportedEncodingException, MessagingException {
		if(! StringUtils.isBlank(this.emailTask.getFromAddress())) {
			msg.setFrom(this.emailTask.getFromAddress(), this.emailTask.getJavaMailSenderImpl().getUsername());
		} else {
			msg.setFrom(this.emailTask.getJavaMailSenderImpl().getUsername());
		}
	}
	
	private String [] processRecipients(String ... emails)
	{
		return Arrays.stream(emails).filter(email -> {
			boolean isValid = validator.isValid(email);
			if(! isValid) {
				LOGGER.info("Not valid recipient specified: " + email);
			}
			return validator.isValid(email);
		}).toArray(String[]::new);
	}
}
