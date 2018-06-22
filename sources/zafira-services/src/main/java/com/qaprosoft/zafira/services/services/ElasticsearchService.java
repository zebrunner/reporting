package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.jmx.IJMXService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchService implements IJMXService
{

	private static final Logger LOGGER = Logger.getLogger(ElasticsearchService.class);

	private RestClient client;

	@Value("${zafira.elasticsearch.host}")
	private String host;

	@Value("${zafira.elasticsearch.port}")
	private String port;

	@Override
	@PostConstruct
	public void init()
	{
		try
		{
			if (! StringUtils.isBlank(host) && ! StringUtils.isBlank(port))
			{
				this.client = RestClient.builder(new HttpHost(host, Integer.valueOf(port))).build();
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

	public List<Setting> getSettings()
	{
		return new ArrayList<Setting>()
		{
			private static final long serialVersionUID = 7140283430898343120L;
			{
				add(new Setting()
				{
					private static final long serialVersionUID = 6585486041064259383L;
					{
						setName("Host");
						setValue(host);
					}
				});
				add(new Setting()
				{
					private static final long serialVersionUID = -7889117639518182523L;
					{
						setName("Port");
						setValue(port);
					}
				});
			}
		};
	}
}
