package com.qaprosoft.zafira.services.services.emails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

public class AsynSendEmailTask implements Runnable
{
	@Autowired
	private JavaMailSender mailSender;

	private MimeMessagePreparator preparator;

	public AsynSendEmailTask(MimeMessagePreparator preparator)
	{
		this.preparator = preparator;
	}

	@Override
	public void run()
	{
		mailSender.send(preparator);
	}
}
