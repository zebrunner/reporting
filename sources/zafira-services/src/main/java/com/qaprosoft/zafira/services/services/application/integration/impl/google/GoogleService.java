/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
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
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.integration.impl.google;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import com.qaprosoft.zafira.services.services.application.integration.impl.CryptoService;
import com.qaprosoft.zafira.services.services.application.integration.impl.google.auth.GoogleDriveAuthService;
import com.qaprosoft.zafira.services.services.application.integration.impl.google.auth.GoogleSheetsAuthService;
import com.qaprosoft.zafira.services.services.application.integration.context.GoogleContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.qaprosoft.zafira.models.db.Setting.Tool.GOOGLE;

@Component
public class GoogleService extends AbstractIntegration<GoogleContext>
{

	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleService.class);

	private final GoogleDriveAuthService driveAuthService;
	private final GoogleSheetsAuthService sheetsAuthService;
	private final SettingsService settingsService;
	private final CryptoService cryptoService;

	public GoogleService(SettingsService settingsService, GoogleDriveAuthService driveAuthService, GoogleSheetsAuthService sheetsAuthService, CryptoService cryptoService) {
		super(GOOGLE);
		this.settingsService = settingsService;
		this.driveAuthService = driveAuthService;
		this.sheetsAuthService = sheetsAuthService;
		this.cryptoService = cryptoService;
	}

	@Override
	public void init()
	{
		String originName = null;
		byte[] credsFile = null;
		boolean enabled = false;

		try {
			List<Setting> googleSettings = settingsService.getSettingsByTool(GOOGLE);
			for (Setting setting : googleSettings) {
				if (setting.isEncrypted()) {
					setting.setValue(cryptoService.decrypt(setting.getValue()));
				}
				switch (Setting.SettingType.valueOf(setting.getName())) {
					case GOOGLE_CLIENT_SECRET_ORIGIN:
						credsFile = setting.getFile();
						originName = setting.getValue();
						break;
					case GOOGLE_ENABLED:
						enabled = Boolean.valueOf(setting.getValue());
						break;
					default:
						break;
				}
			}
			init(credsFile, originName, enabled);
		} catch (Exception e) {
			LOGGER.error("Setting does not exist", e);
		}
	}

	public void init(byte[] credsFile, String originName, boolean enabled) {
		try {
			if (!StringUtils.isEmpty(originName) && credsFile != null) {
				putContext(new GoogleContext(credsFile, originName, enabled));
			}
		} catch (Exception e) {
			LOGGER.error("Unable to initialize Google integration: " + e.getMessage());
		}
	}

	public String getTemporaryAccessToken(Long expiresIn) throws IOException {
		byte[] credsFile = mapContext(GoogleContext::getCredsFile).orElse(null);
		return credsFile != null ? AbstractGoogleService.authorize(credsFile, expiresIn).getAccessToken() : null;
	}

	@Override
	@SuppressWarnings("all")
	public boolean isConnected()
	{
		boolean result = false;
		try
		{
			if(getContext() != null)
			{
				driveAuthService.getService(context().getCredsFile()).about();
				sheetsAuthService.getService(context().getCredsFile()).spreadsheets();
				result = true;
			}
		} catch(Exception e)
		{
		}
		return result;
	}

	/**
	 * Throws an integration exception if integration is not configured
	 * @return google drive service client
	 */
	public GoogleDriveService getDriveService()
	{
		return context().getDriveService();
	}

	/**
	 * Throws an integration exception if integration is not configured
	 * @return google drive service client
	 */
	public GoogleSpreadsheetsService getSpreadsheetsService()
	{
		return context().getSpreadsheetsService();
	}
}
