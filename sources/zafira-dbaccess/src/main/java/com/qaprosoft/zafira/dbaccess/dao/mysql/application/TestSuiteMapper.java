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
package com.qaprosoft.zafira.dbaccess.dao.mysql.application;

import org.apache.ibatis.annotations.Param;

import com.qaprosoft.zafira.models.db.TestSuite;

public interface TestSuiteMapper
{
	void createTestSuite(TestSuite testSuite);

	TestSuite getTestSuiteById(long id);

	TestSuite getTestSuiteByIdFull(long id);

	TestSuite getTestSuiteByName(String name);

	TestSuite getTestSuiteByNameAndFileNameAndUserId(@Param("name") String name, @Param("fileName") String fileName, @Param("userId") long userId);

	void updateTestSuite(TestSuite testSuite);

	void deleteTestSuiteById(long id);

	void deleteTestSuite(TestSuite testSuite);
}
