package com.qaprosoft.zafira.services.services;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestMetricMapper;
import com.qaprosoft.zafira.models.db.TestMetric;

@Service
public class TestMetricService
{
	private static final Logger LOGGER = Logger.getLogger(TestMetricService.class);
	
	@Autowired
	private TestMetricMapper testMetricMapper;
	
	@Transactional(rollbackFor = Exception.class)
	public void createTestMetrics(Long testId, Map<String, Long> testMetrics)
	{
		try
		{
			if(testMetrics != null)
			{
				for(String key : testMetrics.keySet())
				{
					testMetricMapper.createTestMetric(new TestMetric(key, testMetrics.get(key), testId));
				}
			}
		}
		catch(Exception e)
		{
			LOGGER.error("Unable to register test metrics: " + e.getMessage());
		}
	}
}