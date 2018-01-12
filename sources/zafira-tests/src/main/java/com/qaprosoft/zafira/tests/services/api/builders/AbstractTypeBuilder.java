package com.qaprosoft.zafira.tests.services.api.builders;

import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.models.dto.*;
import com.qaprosoft.zafira.tests.services.api.AuthAPIService;
import com.qaprosoft.zafira.tests.util.Config;
import org.apache.log4j.Logger;

import java.util.Random;

public abstract class AbstractTypeBuilder<T extends AbstractType> implements IModelBuilder<T>
{

	protected static final Logger LOGGER = Logger.getLogger(AbstractTypeBuilder.class);

	protected Random random = new Random();
	protected Long userId = 1L;
	protected static final String ZAFIRA_URL = Config.get("zafira_service_url");
	protected static ZafiraClient zafiraClient;
	protected static AuthAPIService authAPIService = new AuthAPIService();

	static
	{
		zafiraClient = new ZafiraClient(ZAFIRA_URL);
		zafiraClient.setAuthToken("Bearer " + authAPIService.getAuthToken());
	}
}
