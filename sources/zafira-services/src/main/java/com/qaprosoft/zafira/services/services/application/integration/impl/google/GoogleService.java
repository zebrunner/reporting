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
import com.qaprosoft.zafira.services.services.application.integration.impl.CryptoService;
import com.qaprosoft.zafira.services.services.application.integration.Integration;
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
public class GoogleService implements Integration<GoogleContext>
{

	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleService.class);

	private final GoogleDriveAuthService driveAuthService;
	private final GoogleSheetsAuthService sheetsAuthService;
	private final SettingsService settingsService;
	private final CryptoService cryptoService;

	private GoogleDriveService driveService;
	private GoogleSpreadsheetsService spreadsheetsService;

	public GoogleService(SettingsService settingsService, GoogleDriveAuthService driveAuthService, GoogleSheetsAuthService sheetsAuthService, CryptoService cryptoService) {
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
				putContext(GOOGLE, new GoogleContext(credsFile, originName, enabled));
				driveService = new GoogleDriveService(credsFile);
				spreadsheetsService = new GoogleSpreadsheetsService(credsFile);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to initialize Google integration: " + e.getMessage());
		}
	}

	public String getTemporaryAccessToken(Long expiresIn) throws IOException {
		String result = null;
		if(getContext() != null) {
			result = AbstractGoogleService.authorize(getContext().getCredsFile(), expiresIn).getAccessToken();
		}
		return result;
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
				driveAuthService.getService(getContext().getCredsFile()).about();
				sheetsAuthService.getService(getContext().getCredsFile()).spreadsheets();
				result = true;
			}
		} catch(Exception e)
		{
		}
		return result;
	}

	public GoogleDriveService getDriveService()
	{
		return driveService;
	}

	public GoogleSpreadsheetsService getSpreadsheetsService()
	{
		return spreadsheetsService;
	}

	public GoogleContext getContext() {
		return getContext(GOOGLE);
	}
}
