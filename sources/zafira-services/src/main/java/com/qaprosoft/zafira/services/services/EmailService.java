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
package com.qaprosoft.zafira.services.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Function;

import javax.mail.internet.MimeMessage;

import com.qaprosoft.zafira.services.exceptions.IntegrationException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.emails.AsynSendEmailTask;
import com.qaprosoft.zafira.services.services.emails.IEmailMessage;
import com.qaprosoft.zafira.services.util.FreemarkerUtil;

import static com.qaprosoft.zafira.models.db.Setting.Tool.EMAIL;

@Service
public class EmailService
{
	private Logger LOGGER = Logger.getLogger(EmailService.class);

	@Autowired
	private FreemarkerUtil freemarkerUtil;
	
	@Autowired
	private AsynSendEmailTask emailTask;
	
	@Autowired
	private AutowireCapableBeanFactory autowireizer;

	@Autowired
	private SettingsService settingsService;
	
	private EmailValidator validator = EmailValidator.getInstance();

	private Function<Void, Boolean> isConnected = aVoid -> settingsService.isConnected(EMAIL);
	
	public String sendEmail(final IEmailMessage message, final String... emails) throws ServiceException
	{

		if(! settingsService.isConnected(EMAIL))
		{
			throw new IntegrationException("SMTP server connection is refused.");
		}

		final String text = freemarkerUtil.getFreeMarkerTemplateContent(message.getTemplate(), message);
		final String [] recipients = processRecipients(emails);

		if(recipients.length > 0)
		{
			MimeMessagePreparator preparator = new MimeMessagePreparator()
			{
				public void prepare(MimeMessage mimeMessage) throws Exception
				{
					boolean hasAttachments = message.getAttachments() != null;

					MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, hasAttachments);
					msg.setSubject(message.getSubject());
					msg.setTo(recipients);
					msg.setFrom(emailTask.getJavaMailSenderImpl().getUsername());
					msg.setText(text, true);
					if(hasAttachments)
					{
						for(Attachment attachment : message.getAttachments())
						{
							msg.addAttachment(attachment.getName() + "." + FilenameUtils.getExtension(attachment.getFile().getName()), attachment.getFile());
							msg.addInline(attachment.getName().replaceAll(" ", "_"), attachment.getFile());
						}
					}
				}
			};
			emailTask.setPreparator(preparator);
			Runnable task = emailTask;
			autowireizer.autowireBean(task);
			Executors.newSingleThreadExecutor().execute(task);
		}
		return text;
	}
	
	private String [] processRecipients(String ... emails)
	{
		List<String> recipients = new ArrayList<>();
		for(String email : emails)
		{
			if(validator.isValid(email))
			{
				recipients.add(email);
			}
			else
			{
				LOGGER.info("Not valid recipient specified: " + email);
			}
		}
		return recipients.toArray(new String[0]);
	}
}
