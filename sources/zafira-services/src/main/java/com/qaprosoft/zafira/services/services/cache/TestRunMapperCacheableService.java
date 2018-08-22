package com.qaprosoft.zafira.services.services.cache;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestRunMapper;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component(value = "testRunMapperCacheableService")
public class TestRunMapperCacheableService implements ICacheableService<Long, TestRunStatistics>
{
	private static final long serialVersionUID = 4700339467519700561L;
	
	@Autowired
	private TestRunMapper testRunMapper;

	@Override
	public Function<Long, TestRunStatistics> getValue()
	{
		return testRunId -> testRunMapper.getTestRunStatistics(testRunId);
	}
}
