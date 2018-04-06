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
package com.qaprosoft.zafira.models.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TestCase extends AbstractEntity
{
	private static final long serialVersionUID = 4877029098773384360L;

	private String testClass;
	private String testMethod;
	private Status status;
	private String info;
	private Long testSuiteId;
	private User primaryOwner = new User();
	private User secondaryOwner = new User();
	private TestSuite testSuite = new TestSuite();
	private Project project;
	private Double stability;

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

	public User getPrimaryOwner()
	{
		return primaryOwner;
	}

	public void setPrimaryOwner(User primaryOwner)
	{
		this.primaryOwner = primaryOwner;
	}

	public User getSecondaryOwner()
	{
		return secondaryOwner;
	}

	public void setSecondaryOwner(User secondaryOwner)
	{
		this.secondaryOwner = secondaryOwner;
	}

	public TestSuite getTestSuite()
	{
		return testSuite;
	}

	public void setTestSuite(TestSuite testSuite)
	{
		this.testSuite = testSuite;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public Project getProject()
	{
		return project;
	}

	public void setProject(Project project)
	{
		this.project = project;
	}

	public Double getStability()
	{
		return stability;
	}

	public void setStability(Double stability)
	{
		this.stability = stability;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj != null && obj instanceof TestCase && this.hashCode() == ((TestCase) obj).hashCode());
	}

	@Override
	public int hashCode()
	{
		return (testClass + testMethod + testSuiteId + info + primaryOwner.getId() + secondaryOwner.getId()).hashCode();
	}
}
