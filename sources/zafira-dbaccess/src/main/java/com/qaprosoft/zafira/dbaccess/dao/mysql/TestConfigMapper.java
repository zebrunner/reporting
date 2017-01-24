package com.qaprosoft.zafira.dbaccess.dao.mysql;

import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.models.db.TestConfig;

public interface TestConfigMapper
{
	void createTestConfig(TestConfig testConfig);

	TestConfig getTestConfigById(long id);
	
	TestConfig searchTestConfig(@Param("testConfig") TestConfig testConfig);
	
	void updateTestConfig(TestConfig testConfig);

	void deleteTestConfigById(long id);
}
