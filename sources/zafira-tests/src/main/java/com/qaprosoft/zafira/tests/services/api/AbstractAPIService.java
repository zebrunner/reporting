package com.qaprosoft.zafira.tests.services.api;

import java.util.Random;

import org.apache.log4j.Logger;

import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.tests.util.Config;

public abstract class AbstractAPIService
{

	protected static final Logger LOGGER = Logger.getLogger(AuthAPIService.class);
	protected static final String ZAFIRA_URL = Config.get("zafira_service_url");
	protected static final ZafiraClient ZAFIRA_CLIENT;
	protected static final AuthAPIService AUTH_API_SERVICE = new AuthAPIService();

	static
	{
		ZAFIRA_CLIENT = new ZafiraClient(ZAFIRA_URL);
		ZAFIRA_CLIENT.setAuthToken("Bearer " + AUTH_API_SERVICE.getAuthToken());
	}

	protected Random random = new Random();

	protected UserType USER = new UserType(){
		{
			setId(1L);
		}
	};//ZAFIRA_CLIENT.getUserProfile().getObject();

}
