package com.qaprosoft.zafira.services.services.jmx.google.auth;

import com.google.api.services.sheets.v4.Sheets;
import com.qaprosoft.zafira.services.services.jmx.google.AbstractGoogleService;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleSheetsAuthService extends AbstractGoogleService
{

	public static Sheets getService() throws IOException
	{
		return new Sheets.Builder(getHttpTransport(), getJsonFactory(), authorize())
				.setApplicationName(getApplicationName())
				.build();
	}
}
