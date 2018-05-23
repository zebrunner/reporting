package com.qaprosoft.zafira.services.services.jmx.google;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import com.qaprosoft.zafira.services.services.jmx.google.auth.GoogleDriveAuthService;

import java.io.IOException;

public class GoogleDriveService extends AbstractGoogleService
{

	private Drive driveService;

	public GoogleDriveService()
	{
		try
		{
			this.driveService = GoogleDriveAuthService.getService();
		} catch (IOException e)
		{
			LOGGER.error(e);
		}
	}

	public enum GranteeType
	{
		USER("user"), GROUP("group"), DOMAIN("domain"), ANYONE("anyone");

		private String value;

		GranteeType(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	public enum GranteeRoleType
	{
		ORGANIZER("organizer"), OWNER("owner"), WRITER("writer"), COMMENTER("commenter"), READER("reader");

		private String value;

		GranteeRoleType(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	public void shareFile(String fileId, GranteeType granteeType, GranteeRoleType granteeRoleType, String email)
	{
		BatchRequest batch = new BatchRequest(getHttpTransport(), (HttpRequestInitializer) driveService.getGoogleClientRequestInitializer());
		Permission userPermission = new Permission()
				.setType(granteeType.getValue())
				.setRole(granteeRoleType.getValue())
				.setEmailAddress(email);
		try
		{
			driveService.permissions().create(fileId, userPermission)
					.setFields("id")
					.queue(batch, new GoogleDriveJsonBatchCallback<Permission>());
			batch.execute();
		} catch (IOException e)
		{
			LOGGER.error(e);
		}
	}

	private class GoogleDriveJsonBatchCallback<T> extends JsonBatchCallback<T>
	{
		@Override
		public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
			LOGGER.error(e.getMessage());
		}

		@Override
		public void onSuccess(T t, HttpHeaders responseHeaders) throws IOException {
		}
	}
}
