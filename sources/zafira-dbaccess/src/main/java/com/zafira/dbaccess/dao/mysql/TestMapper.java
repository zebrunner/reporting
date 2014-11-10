package com.zafira.dbaccess.dao.mysql;

import com.zafira.dbaccess.model.Test;

public interface TestMapper
{
	void createTest(Test test);

	Test getTestById(long id);

	void updateTest(Test test);

	void deleteTestById(long id);

	void deleteTest(Test test);
}
