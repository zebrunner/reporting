/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
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

import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import com.qaprosoft.zafira.services.services.application.integration.impl.CryptoService;
import com.qaprosoft.zafira.services.services.application.integration.impl.google.auth.GoogleDriveAuthService;
import com.qaprosoft.zafira.services.services.application.integration.impl.google.auth.GoogleSheetsAuthService;
import com.qaprosoft.zafira.services.services.application.integration.context.GoogleContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.qaprosoft.zafira.models.db.Setting.Tool.GOOGLE;

@Component
public class GoogleService extends AbstractIntegration<GoogleContext>
{

	private final GoogleDriveAuthService driveAuthService;
	private final GoogleSheetsAuthService sheetsAuthService;

	public GoogleService(SettingsService settingsService, GoogleDriveAuthService driveAuthService, GoogleSheetsAuthService sheetsAuthService, CryptoService cryptoService) {
		super(settingsService, cryptoService, GOOGLE, GoogleContext.class);
		this.driveAuthService = driveAuthService;
		this.sheetsAuthService = sheetsAuthService;
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
