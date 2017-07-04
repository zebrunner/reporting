package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import com.qaprosoft.zafira.models.db.TestArtifact;

public interface TestArtifactMapper
{
	void createTestArtifact(TestArtifact testArtifact);
	
	TestArtifact getTestArtifactById(long id);

	List<TestArtifact> getTestArtifactsByTestId(long testId);

	void updateTestArtifact(TestArtifact testArtifact);

	void deleteTestArtifactById(long id);
	
	void deleteTestArtifactsByTestId(long testId);
}
