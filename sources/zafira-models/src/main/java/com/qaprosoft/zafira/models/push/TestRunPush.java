package com.qaprosoft.zafira.models.push;

import com.qaprosoft.zafira.models.db.TestRun;

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
