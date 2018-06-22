package com.qaprosoft.zafira.services.services.jmx;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.SettingsService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

import static com.qaprosoft.zafira.models.db.Setting.Tool.ELASTICSEARCH;

@ManagedResource(objectName = "bean:name=elasticsearchService", description = "Elasticsearch init Managed Bean",
		currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200)
public class ElasticsearchService implements IJMXService
{

	private static final Logger LOGGER = Logger.getLogger(ElasticsearchService.class);

	private RestClient client;

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private CryptoService cryptoService;

	@Override
	@PostConstruct
	public void init()
	{
		String url = null;

		try
		{
			List<Setting> jiraSettings = settingsService.getSettingsByTool(ELASTICSEARCH);
			for (Setting setting : jiraSettings)
			{
				if (setting.isEncrypted())
				{
					setting.setValue(cryptoService.decrypt(setting.getValue()));
				}
				switch (Setting.SettingType.valueOf(setting.getName()))
				{
				case ELASTICSEARCH_URL:
					url = setting.getValue();
					break;
				default:
					break;
				}
			}
			init(url);
		} catch (Exception e)
		{
			LOGGER.error("Setting does not exist", e);
		}
	}

	@ManagedOperation(description = "Elasticsearch initialization")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "url", description = "Elasticsearch url")})
	public void init(String url)
	{
		try
		{
			if (!StringUtils.isEmpty(url))
			{
				this.client = RestClient.builder(HttpHost.create(url)).build();
			}
		} catch (Exception e)
		{
			LOGGER.error("Unable to initialize Elasticsearch integration: " + e.getMessage());
		}
	}

	@Override
	public boolean isConnected()
	{
		try
		{
			return client != null && client.performRequest("GET", "/") != null;
		} catch (IOException e)
		{
			return false;
		}
	}
}
