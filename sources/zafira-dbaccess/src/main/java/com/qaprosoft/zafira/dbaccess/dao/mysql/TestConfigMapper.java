package com.qaprosoft.zafira.dbaccess.dao.mysql;

import com.qaprosoft.zafira.dbaccess.model.TestConfig;

public interface TestConfigMapper
{
	void createTestConfig(TestConfig testConfig);

	TestConfig getTestConfigById(long id);
	
	void updateTestConfig(TestConfig testConfig);

	void deleteTestConfigById(long id);
}
