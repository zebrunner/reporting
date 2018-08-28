/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.models.push;

import com.qaprosoft.zafira.models.dto.TestRunStatistics;

public class TestRunStatisticPush extends AbstractPush
{
	private TestRunStatistics testRunStatistics;

	public TestRunStatisticPush(TestRunStatistics testRunStatistics)
	{
		super(Type.TEST_RUN_STATISTICS);
		this.testRunStatistics = testRunStatistics;
	}

	public TestRunStatistics getTestRunStatistics()
	{
		return testRunStatistics;
	}

	public void setTestRunStatistics(TestRunStatistics testRunStatistics)
	{
		this.testRunStatistics = testRunStatistics;
	}
}
