package com.qaprosoft.zafira.services.services;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.emails.IEmailMessage;

import freemarker.template.Configuration;

@Service
public class EmailService
{
	private Logger LOGGER = Logger.getLogger(EmailService.class);
	
	@Value("${mail.user}")
	private String mailUser;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private Configuration freemarkerConfiguration;
	
	
	public void sendEmail(final IEmailMessage message, final String... receipients)
	{
		MimeMessagePreparator preparator = new MimeMessagePreparator()
		{
			public void prepare(MimeMessage mimeMessage) throws Exception
			{
				MimeMessageHelper msg = new MimeMessageHelper(mimeMessage);
				msg.setSubject(message.getSubject());
				msg.setTo(receipients);
				msg.setFrom(mailUser);
				msg.setText(getFreeMarkerTemplateContent(message), true);
			}
		};
		mailSender.send(preparator);
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
