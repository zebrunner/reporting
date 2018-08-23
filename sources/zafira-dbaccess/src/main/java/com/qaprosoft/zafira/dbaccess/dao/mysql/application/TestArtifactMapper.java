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

import java.util.List;

import com.qaprosoft.zafira.models.db.application.TestArtifact;
import org.apache.ibatis.annotations.Param;

public interface TestArtifactMapper
{
	void createTestArtifact(TestArtifact testArtifact);
	
	TestArtifact getTestArtifactById(long id);

	List<TestArtifact> getTestArtifactsByTestId(long testId);

	TestArtifact getTestArtifactByNameAndTestId (@Param("name") String name,@Param("testId")  long testId);

	void updateTestArtifact(TestArtifact testArtifact);

	void deleteTestArtifactById(long id);
	
	void deleteTestArtifactsByTestId(long testId);
}
