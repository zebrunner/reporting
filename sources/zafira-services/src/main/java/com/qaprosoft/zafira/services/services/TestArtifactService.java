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
package com.qaprosoft.zafira.services.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestArtifactMapper;
import com.qaprosoft.zafira.models.db.TestArtifact;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class TestArtifactService
{
	@Autowired
	private TestArtifactMapper testArtifactMapper;
	
	@Transactional(rollbackFor = Exception.class)
	public void createTestArtifact(TestArtifact testArtifact) throws ServiceException
	{
		testArtifactMapper.createTestArtifact(testArtifact);
	}
	
	@Transactional(readOnly = true)
	public List<TestArtifact> getAllTestArtifacts(Long testId) throws ServiceException
	{
		return testArtifactMapper.getTestArtifactsByTestId(testId);
	}
	
	@Transactional(readOnly = true)
	public TestArtifact getTestArtifactById(long id) throws ServiceException
	{
		return testArtifactMapper.getTestArtifactById(id);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestArtifact updateTestArtifact(TestArtifact testArtifact) throws ServiceException
	{
		testArtifactMapper.updateTestArtifact(testArtifact);
		return testArtifact;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestArtifactById(Long id) throws ServiceException
	{
		testArtifactMapper.deleteTestArtifactById(id);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestArtifactsByTestId(Long testId) throws ServiceException
	{
		testArtifactMapper.deleteTestArtifactsByTestId(testId);
	}

	@Transactional(readOnly = true)
	public TestArtifact getTestArtifactByNameAndTestId(String name, long testId) throws ServiceException
	{
        TestArtifact testArtifact = testArtifactMapper.getTestArtifactByNameAndTestId(name, testId);
		return testArtifact;
	}

	@Transactional(rollbackFor = Exception.class)
	public TestArtifact createOrUpdateTestArtifact(TestArtifact newTestArtifact) throws ServiceException
	{
		TestArtifact testArtifact = getTestArtifactByNameAndTestId(newTestArtifact.getName(), newTestArtifact.getTestId());
		if(testArtifact == null)
		{
			createTestArtifact(newTestArtifact);
		}
		else if(!testArtifact.equals(newTestArtifact))
		{
			newTestArtifact.setId(testArtifact.getId());
			updateTestArtifact(newTestArtifact);
		}
		else
		{
			newTestArtifact = testArtifact;
		}
		return newTestArtifact;
	}
}
