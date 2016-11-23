package com.qaprosoft.zafira.services.services;

import java.util.concurrent.Executors;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.emails.AsynSendEmailTask;
import com.qaprosoft.zafira.services.services.emails.IEmailMessage;

import freemarker.template.Configuration;

@Service
public class EmailService
{
	private Logger LOGGER = Logger.getLogger(EmailService.class);
	
	@Value("${mail.user}")
	private String mailUser;
	
	@Autowired
	private Configuration freemarkerConfiguration;
	
	@Autowired
	private AutowireCapableBeanFactory autowireizer;
	
	public String sendEmail(final IEmailMessage message, final String... recipients) throws ServiceException
	{
		final String text = getFreeMarkerTemplateContent(message);
		MimeMessagePreparator preparator = new MimeMessagePreparator()
		{
			public void prepare(MimeMessage mimeMessage) throws Exception
			{
				boolean hasAttachment = message.getAttachment() != null;
				
				MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, hasAttachment);
				msg.setSubject(message.getSubject());
				msg.setTo(recipients);
				msg.setFrom(mailUser);
				msg.setText(text, true);
				if(hasAttachment)
				{
					msg.addAttachment("attachment", message.getAttachment());
				}
			}
		};
		Runnable task = new AsynSendEmailTask(preparator);
		autowireizer.autowireBean(task);
		Executors.newSingleThreadExecutor().execute(task);
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
