/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.cache;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TestRunMapper;
import com.qaprosoft.zafira.models.dto.application.TestRunStatistics;
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
