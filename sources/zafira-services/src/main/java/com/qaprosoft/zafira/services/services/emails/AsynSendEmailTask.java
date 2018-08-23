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
import com.qaprosoft.zafira.services.services.jmx.models.EmailType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.*;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.MessagingException;
import java.util.List;

import static com.qaprosoft.zafira.models.db.Setting.Tool.EMAIL;

@ManagedResource(objectName = "bean:name=asyncSendEmailTask", description = "Email init Managed Bean",
		currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200)
public class AsynSendEmailTask implements Runnable, IJMXService<EmailType>
{

	private static final Logger LOGGER = Logger.getLogger(AsynSendEmailTask.class);

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private CryptoService cryptoService;

	private MimeMessagePreparator preparator;

	@Override
	public void run()
	{
		getJavaMailSenderImpl().send(preparator);
	}

	@Autowired
	public void init() {
		String host = null;
		int port = 0;
		String user = null;
		String fromAddress = null;
		String password = null;
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
						host = setting.getValue();
						break;
					case EMAIL_PORT:
						port = StringUtils.isBlank(setting.getValue()) ? 0 : Integer.valueOf(setting.getValue());
						break;
					case EMAIL_USER:
						user = setting.getValue();
						break;
					case EMAIL_FROM_ADDRESS:
						fromAddress = setting.getValue();
						break;
					case EMAIL_PASSWORD:
						password = setting.getValue();
						break;
					default:
						break;
				}
			}
			init(host, port, user, fromAddress, password);
		} catch(Exception e) {
			LOGGER.error("Setting does not exist", e);
		}
	}

	@ManagedOperation(description = "Change SMTP initialization")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "host", description = "SMTP host"),
			@ManagedOperationParameter(name = "port", description = "SMTP port"),
			@ManagedOperationParameter(name = "user", description = "SMTP user"),
			@ManagedOperationParameter(name = "fromAddress", description = "SMTP from address"),
			@ManagedOperationParameter(name = "password", description = "SMTP password") })
	public void init(String host, int port, String user, String fromAddress, String password) {
		try {
			if (! StringUtils.isBlank(host) && ! StringUtils.isBlank(user) && ! StringUtils.isBlank(password) && port != 0) {
				putType(EMAIL, new EmailType(host, port, user, fromAddress, password));
			}
		} catch (Exception e) {
			LOGGER.error("Unable to initialize SMTP integration: " + e.getMessage());
		}
	}

	@Override
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
		return getType(EMAIL) != null ? (JavaMailSenderImpl) getType(EMAIL).getJavaMailSender() : null;
	}

	public String getFromAddress()
	{
		return getType(EMAIL) != null ? getType(EMAIL).getFromAddress() : null;
	}
}
