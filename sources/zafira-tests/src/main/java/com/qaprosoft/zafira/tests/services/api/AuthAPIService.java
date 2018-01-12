package com.qaprosoft.zafira.tests.services.api;

import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.tests.util.Config;

public class AuthAPIService
{
	private ZafiraClient zafiraClient = new ZafiraClient(Config.get("zafira_service_url"));

	public String getAuthToken()
	{
		return zafiraClient.login(Config.get("admin1.user"), Config.get("admin1.pass")).getObject().getAccessToken();
	}
}
