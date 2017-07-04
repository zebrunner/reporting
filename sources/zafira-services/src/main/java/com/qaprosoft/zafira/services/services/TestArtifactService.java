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
}
