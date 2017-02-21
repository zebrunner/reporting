package com.qaprosoft.zafira.services.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestMetricMapper;
import com.qaprosoft.zafira.models.db.TestMetric;

@Service
public class TestMetricService
{
	@Autowired
	private TestMetricMapper testMetricMapper;
	
	@Transactional(rollbackFor = Exception.class)
	public void createTestMetric(TestMetric testMetric)
	{
		testMetricMapper.createTestMetric(testMetric);
	}
}