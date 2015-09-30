package com.qaprosoft.zafira.services.services;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestRunMapper;
import com.qaprosoft.zafira.dbaccess.model.Test;
import com.qaprosoft.zafira.dbaccess.model.TestRun;
import com.qaprosoft.zafira.dbaccess.model.TestRun.Status;
import com.qaprosoft.zafira.dbaccess.model.config.Argument;
import com.qaprosoft.zafira.dbaccess.model.config.Configuration;
import com.qaprosoft.zafira.services.exceptions.InvalidTestRunException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.exceptions.TestRunNotFoundException;

@Service
public class TestRunService
{
	private Logger logger = Logger.getLogger(TestRunService.class);
	
	@Autowired
	private TestRunMapper testRunMapper;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TestService testService;

	@Autowired
	private WorkItemService workItemService;
	
	private Unmarshaller unmarshaller;
	
	public TestRunService()
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
	public void createTestRun(TestRun testRun) throws ServiceException
	{
		testRunMapper.createTestRun(testRun);
	}
	
	@Transactional(readOnly = true)
	public TestRun getTestRunById(long id) throws ServiceException
	{
		return testRunMapper.getTestRunById(id);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestRun updateTestRun(TestRun testRun) throws ServiceException
	{
		testRunMapper.updateTestRun(testRun);
		return testRun;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteTestRun(TestRun testRun) throws ServiceException
	{
		testRunMapper.deleteTestRun(testRun);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestRun initializeTestRun(TestRun newTestRun) throws ServiceException, JAXBException
	{
		switch (newTestRun.getStartedBy())
		{
			case HUMAN:
				if(newTestRun.getUser() == null)
				{
					throw new InvalidTestRunException("Specify userName if started by HUMAN!");
				}
				break;
			case SCHEDULER:
				newTestRun.setUpstreamJobBuildNumber(null);
				newTestRun.setUpstreamJob(null);
				newTestRun.setUser(null);
				break;
			case UPSTREAM_JOB:
				if(newTestRun.getUpstreamJob() == null || newTestRun.getUpstreamJobBuildNumber() == null)
				{
					throw new InvalidTestRunException("Specify upstreamJobId and upstreaBuildNumber if started by UPSTREAM_JOB!");
				}

				// Preparation for rerun if test run exists
				List<TestRun> existingTestRuns = testRunMapper.getTestRunsForRerun(newTestRun.getTestSuiteId(), 
																		   newTestRun.getJob().getId(), 
																		   newTestRun.getUpstreamJob().getId(), 
																		   newTestRun.getUpstreamJobBuildNumber(),
																		   readUniqueArgs(newTestRun.getConfigXML()));
				for(TestRun tr : existingTestRuns)
				{
					for(Test test : testService.getTestsByTestRunId(tr.getId()))
					{
						testService.deleteTestWorkItemByTestId(test.getId());
						testService.deleteTest(test);
					}
					deleteTestRun(tr);
				}
				break;
		}
		newTestRun.setStatus(Status.IN_PROGRESS);
		if(newTestRun.getWorkItem() != null && !StringUtils.isEmpty(newTestRun.getWorkItem().getJiraId()))
		{
			newTestRun.setWorkItem(workItemService.createOrGetWorkItem(newTestRun.getWorkItem()));
		}
		createTestRun(newTestRun);
		return newTestRun;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public TestRun finilizeTestRun(long id) throws ServiceException
	{
		TestRun testRun = getTestRunById(id);
		if(testRun == null)
		{
			throw new TestRunNotFoundException();
		}
		List<Test> tests = testService.getTestsByTestRunId(testRun.getId());
		testRun.setStatus(Status.PASSED);
		for(Test test : tests)
		{
			if(test.getStatus().equals(com.qaprosoft.zafira.dbaccess.model.Test.Status.FAILED))
			{
				testRun.setStatus(Status.FAILED);
			}
		}
		updateTestRun(testRun);
		return testRun;
	}
	
	public List<Argument> readUniqueArgs(String configXML)
	{
		List<Argument> uniqueArgs = new ArrayList<>();
		try
		{
			if(!StringUtils.isEmpty(configXML))
			{
				Configuration config = (Configuration) unmarshaller.unmarshal(new ByteArrayInputStream(configXML.getBytes()));
				for(Argument arg : config.getArg())
				{
					if(arg.getUnique())
					{
						uniqueArgs.add(arg);
					}
				}
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
		return uniqueArgs;
	}
}
