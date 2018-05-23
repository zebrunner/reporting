package com.qaprosoft.zafira.services.services.jmx.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractGoogleService
{

	protected static final Logger LOGGER = Logger.getLogger(AbstractGoogleService.class);

	private static String APPLICATION_NAME;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);
	private static final String CLIENT_SECRET_DIR = "./client_secret.json";

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

	public static Credential authorize() throws IOException
	{
		ResourceLoader resourceLoader = new FileSystemResourceLoader();
		return GoogleCredential.fromStream(resourceLoader.getResource(CLIENT_SECRET_DIR).getInputStream()).createScoped(SCOPES);
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

	public static String getClientSecretDir()
	{
		return CLIENT_SECRET_DIR;
	}

	public static NetHttpTransport getHttpTransport()
	{
		return HTTP_TRANSPORT;
	}
}
