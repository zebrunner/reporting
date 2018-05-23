package com.qaprosoft.zafira.services.services.jmx.google.auth;

import com.google.api.services.drive.Drive;
import com.qaprosoft.zafira.services.services.jmx.google.AbstractGoogleService;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleDriveAuthService extends AbstractGoogleService
{

	public static Drive getService() throws IOException
	{
		return new Drive.Builder(getHttpTransport(), getJsonFactory(), authorize())
				.setApplicationName(getApplicationName())
				.build();
	}
}
