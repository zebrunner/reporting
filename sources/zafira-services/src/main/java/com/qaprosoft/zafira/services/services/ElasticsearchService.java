package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.jmx.IJMXService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchService implements IJMXService
{

	@Value("${zafira.elasticsearch.url}")
	private String url;

	@Value("${zafira.elasticsearch.user}")
	private String user;

	@Value("${zafira.elasticsearch.pass}")
	private String password;

	@Override
	public void init()
	{
	}

	@Override
	public boolean isConnected()
	{
		return true;
	}

	public List<Setting> getSettings()
	{
		return new ArrayList<Setting>()
		{
			private static final long serialVersionUID = 7140283430898343120L;
			{
				add(new Setting()
				{
					private static final long serialVersionUID = 658548604106441383L;
					{
						setName("URL");
						setValue(url);
					}
				});
				add(new Setting()
				{
					private static final long serialVersionUID = 6585486043214259383L;
					{
						setName("user");
						setValue(user);
					}
				});
				add(new Setting()
				{
					private static final long serialVersionUID = 6585486425564259383L;
					{
						setName("password");
						setValue(password);
					}
				});
			}
		};
	}
}
