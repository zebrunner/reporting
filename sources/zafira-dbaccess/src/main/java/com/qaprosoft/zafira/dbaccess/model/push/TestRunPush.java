package com.qaprosoft.zafira.dbaccess.model.push;

import com.qaprosoft.zafira.dbaccess.model.TestRun;

public class TestRunPush extends AbstractPush {

	private TestRun testRun;

	public TestRunPush(TestRun testRun) {
		super(Type.TEST_RUN);
		this.testRun = testRun;
	}

	public TestRun getTestRun() {
		return testRun;
	}

	public void setTestRun(TestRun testRun) {
		this.testRun = testRun;
	}
}
