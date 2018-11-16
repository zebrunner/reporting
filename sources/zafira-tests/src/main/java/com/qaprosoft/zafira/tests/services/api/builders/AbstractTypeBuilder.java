package com.qaprosoft.zafira.tests.services.api.builders;

import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.models.dto.AbstractType;
import com.qaprosoft.zafira.tests.services.api.AuthAPIService;
import com.qaprosoft.zafira.tests.util.Config;

public abstract class AbstractTypeBuilder<T extends AbstractType> implements IModelBuilder<T>
{

	protected static final Logger LOGGER = Logger.getLogger(AbstractTypeBuilder.class);

	protected final Random random = new Random();
	protected static final Long userId = 1L;
	protected static final String ZAFIRA_URL = Config.get("zafira_service_url");
	protected static final ZafiraClient zafiraClient;
	protected static final AuthAPIService authAPIService = new AuthAPIService();

	static
	{
		zafiraClient = new ZafiraClient(ZAFIRA_URL);
		zafiraClient.setAuthToken("Bearer " + authAPIService.getAuthToken());
	}

	public int getNextRandomInt()
	{
		return random.nextInt(10000);
	}

	public String getNextRandomString(int count)
	{
		return RandomStringUtils.randomAlphabetic(count);
	}
}
