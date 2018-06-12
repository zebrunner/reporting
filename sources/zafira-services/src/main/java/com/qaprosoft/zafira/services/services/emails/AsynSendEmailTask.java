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
package com.qaprosoft.zafira.services.services.emails;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.services.services.jmx.CryptoService;
import com.qaprosoft.zafira.services.services.jmx.IJMXService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.util.List;

import static com.qaprosoft.zafira.models.db.Setting.Tool.EMAIL;

@ManagedResource(objectName = "bean:name=asyncSendEmailTask", description = "Email init Managed Bean",
		currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200)
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

	/*public AsynSendEmailTask() {
	}

	public AsynSendEmailTask(MimeMessagePreparator preparator)
	{
		this.preparator = preparator;
	}*/

	@Override
	public void run()
	{
		mailSender.send(preparator);
	}

	@Autowired
	@PostConstruct
	@ManagedOperation(description = "Email initialization")
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
						getJavaMailSenderImpl().setPort(StringUtils.isBlank(setting.getValue()) ? 0 : Integer.valueOf(setting.getValue()));
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

	@ManagedAttribute(description = "Get email server")
	public JavaMailSenderImpl getJavaMailSenderImpl() {
		return (JavaMailSenderImpl)this.mailSender;
	}
}
