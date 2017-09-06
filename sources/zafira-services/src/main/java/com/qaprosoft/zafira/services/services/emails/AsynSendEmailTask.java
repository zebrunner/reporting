package com.qaprosoft.zafira.services.services.emails;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.services.services.jmx.CryptoService;
import com.qaprosoft.zafira.services.services.jmx.IJMXService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.util.List;

import static com.qaprosoft.zafira.models.db.Setting.Tool.EMAIL;

public class AsynSendEmailTask implements Runnable, IJMXService
{

	private static final Logger LOGGER = Logger.getLogger(AsynSendEmailTask.class);

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private CryptoService cryptoService;

	private MimeMessagePreparator preparator;

	@Override
	public void run()
	{
		mailSender.send(preparator);
	}

	@Autowired
	@PostConstruct
	public void init() {
		try {
			List<Setting> emailSettings = settingsService.getSettingsByTool(EMAIL);
			for (Setting setting : emailSettings)
			{
				if(setting.isEncrypted())
				{
					setting.setValue(cryptoService.decrypt(setting.getValue()));
				}
				switch (Setting.SettingType.valueOf(setting.getName()))
				{
					case EMAIL_HOST:
						getJavaMailSenderImpl().setHost(setting.getValue());
						break;
					case EMAIL_PORT:
						getJavaMailSenderImpl().setPort(Integer.valueOf(setting.getValue()));
						break;
					case EMAIL_USER:
						getJavaMailSenderImpl().setUsername(setting.getValue());
						break;
					case EMAIL_PASSWORD:
						getJavaMailSenderImpl().setPassword(setting.getValue());
						break;
					default:
						break;
				}
			}
		} catch(Exception e) {
			LOGGER.error("Setting does not exist", e);
		}
	}

	@Autowired
	public boolean isConnected()
	{
		try {
			getJavaMailSenderImpl().testConnection();
			return true;
		} catch (MessagingException e) {
			return false;
		}
	}

	public void setPreparator(MimeMessagePreparator preparator) {
		this.preparator = preparator;
	}

	public JavaMailSenderImpl getJavaMailSenderImpl() {
		return (JavaMailSenderImpl)this.mailSender;
	}
}
