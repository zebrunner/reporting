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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TestConfigMapper;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestConfig;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.models.db.config.Configuration;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class TestConfigService
{
	private Logger logger = Logger.getLogger(TestConfigService.class);
	
	@Autowired
	private TestConfigMapper testConfigMapper;
	
	@Autowired
	private TestRunService testRunService;
	
	private Unmarshaller unmarshaller;
	
	public TestConfigService()
	{
		JAXBContext context;
		try
		{
			context = JAXBContext.newInstance(Configuration.class);
			unmarshaller = context.createUnmarshaller();
		} catch (JAXBException e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void createTestConfig(TestConfig testConfig) throws ServiceException
	{
		testConfigMapper.createTestConfig(testConfig);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestConfig createTestConfigForTest(Test test, String testConfigXML) throws ServiceException
	{
		TestRun testRun = testRunService.getTestRunById(test.getTestRunId());
		if(testRun == null)
		{
			throw new ServiceException("Test run not found!");
		}
		
		List<Argument> testRunConfig = readConfigArgs(testRun.getConfigXML());
		List<Argument> testConfig = readConfigArgs(testConfigXML);
		
		TestConfig config = new TestConfig().init(testRunConfig).init(testConfig);

		TestConfig existingTestConfig = searchTestConfig(config);
		if(existingTestConfig != null)
		{
			config = existingTestConfig;
		}
		else
		{
			createTestConfig(config);
		}
		
		return config;
	}
	
	@Transactional(readOnly = true)
	public TestConfig getTestConfigById(long id) throws ServiceException
	{
		return testConfigMapper.getTestConfigById(id);
	}
	
	@Transactional(readOnly = true)
	public TestConfig searchTestConfig(TestConfig testConfig) throws ServiceException
	{
		return testConfigMapper.searchTestConfig(testConfig);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteTestConfigById(long id) throws ServiceException
	{
		testConfigMapper.deleteTestConfigById(id);
	}
	
	public List<Argument> readConfigArgs(String configXML)
	{
		List<Argument> args = new ArrayList<>();
		try
		{
			if(!StringUtils.isEmpty(configXML))
			{
				Configuration config = (Configuration) unmarshaller.unmarshal(new ByteArrayInputStream(configXML.getBytes()));
				args.addAll(config.getArg());
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
		return args;
	}
}
