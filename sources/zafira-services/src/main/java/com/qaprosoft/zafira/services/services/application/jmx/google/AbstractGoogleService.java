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
package com.qaprosoft.zafira.services.services.application.jmx.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractGoogleService
{

	protected static final Logger LOGGER = Logger.getLogger(AbstractGoogleService.class);

	private static String APPLICATION_NAME;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);

	private static NetHttpTransport HTTP_TRANSPORT;

	static
	{
		try
		{
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
	}

	public static Credential authorize(byte[] credsFile) throws IOException
	{
		return GoogleCredential.fromStream(new ByteArrayInputStream(credsFile)).createScoped(SCOPES);
	}

	public static String getApplicationName()
	{
		return APPLICATION_NAME;
	}

	public static void setApplicationName(String applicationName)
	{
		APPLICATION_NAME = applicationName;
	}

	public static JsonFactory getJsonFactory()
	{
		return JSON_FACTORY;
	}

	public static List<String> getScopes()
	{
		return SCOPES;
	}

	protected static NetHttpTransport getHttpTransport()
	{
		return HTTP_TRANSPORT;
	}
}
