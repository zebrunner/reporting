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
package com.qaprosoft.zafira.models.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TestCaseType extends AbstractType
{
	private static final long serialVersionUID = 4361075320159665047L;
	@NotNull
	private String testClass;
	@NotNull
	private String testMethod;
	private String info;
	@NotNull
	private Long testSuiteId;
	@NotNull
	private Long primaryOwnerId;
	private Long secondaryOwnerId;
	private ProjectType project;

	public TestCaseType() {
		
	}
	
	public TestCaseType(String testClass, String testMethod, String info, Long testSuiteId, Long primaryOwnerId)
	{
		this.testClass = testClass;
		this.testMethod = testMethod;
		this.info = info;
		this.testSuiteId = testSuiteId;
		this.primaryOwnerId = primaryOwnerId;
	}
	
	public TestCaseType(String testClass, String testMethod, String info, Long testSuiteId, Long primaryOwnerId, Long secondaryUserId)
	{
		this(testClass, testMethod, info, testSuiteId, primaryOwnerId);
		this.secondaryOwnerId = secondaryUserId;
	}
	
	public String getTestClass()
	{
		return testClass;
	}

	public void setTestClass(String testClass)
	{
		this.testClass = testClass;
	}

	public String getTestMethod()
	{
		return testMethod;
	}

	public void setTestMethod(String testMethod)
	{
		this.testMethod = testMethod;
	}

	public String getInfo()
	{
		return info;
	}

	public void setInfo(String info)
	{
		this.info = info;
	}

	public Long getTestSuiteId()
	{
		return testSuiteId;
	}

	public void setTestSuiteId(Long testSuiteId)
	{
		this.testSuiteId = testSuiteId;
	}

	public Long getPrimaryOwnerId()
	{
		return primaryOwnerId;
	}

	public void setPrimaryOwnerId(Long primaryOwnerId)
	{
		this.primaryOwnerId = primaryOwnerId;
	}

	public Long getSecondaryOwnerId()
	{
		return secondaryOwnerId;
	}

	public void setSecondaryOwnerId(Long secondaryOwnerId)
	{
		this.secondaryOwnerId = secondaryOwnerId;
	}

	public ProjectType getProject()
	{
		return project;
	}

	public void setProject(ProjectType project)
	{
		this.project = project;
	}
}
