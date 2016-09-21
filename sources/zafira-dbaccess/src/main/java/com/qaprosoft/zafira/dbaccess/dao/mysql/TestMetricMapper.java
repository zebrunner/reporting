package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.dbaccess.model.TestMetric;

public interface TestMetricMapper
{
	void createTestMetric(TestMetric user);

	TestMetric getTestMetricById(long id);

	void deleteTestMetricById(long id);
}
