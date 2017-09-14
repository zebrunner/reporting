package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.emails.AsynSendEmailTask;
import com.qaprosoft.zafira.services.services.emails.IEmailMessage;
import freemarker.template.Configuration;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.Executors;

@Service
public class EmailService
{
	private Logger LOGGER = Logger.getLogger(EmailService.class);
	
	@Autowired
	private Configuration freemarkerConfiguration;

	@Autowired
	private AsynSendEmailTask emailTask;
	
	@Autowired
	private AutowireCapableBeanFactory autowireizer;
	
	public String sendEmail(final IEmailMessage message, final String... recipients) throws ServiceException
	{
		final String text = getFreeMarkerTemplateContent(message);
		if(recipients != null && recipients.length > 0)
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
			Runnable task = new AsynSendEmailTask(preparator);
			autowireizer.autowireBean(task);
			Executors.newSingleThreadExecutor().execute(task);
		}
		return text;
	}
	
	
	public String getFreeMarkerTemplateContent(IEmailMessage message) throws ServiceException
	{
		StringBuffer content = new StringBuffer();
		try
		{
			content.append(FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(message.getTemplate()), message));
		} catch (Exception e)
		{
			LOGGER.error("Problem with email template compilation: " + e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return content.toString();
	}
}
