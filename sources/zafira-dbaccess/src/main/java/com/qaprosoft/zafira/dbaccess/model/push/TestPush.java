package com.qaprosoft.zafira.dbaccess.model.push;

import org.apache.commons.lang3.StringUtils;

import com.qaprosoft.zafira.dbaccess.model.Test;

public class TestPush extends AbstractPush {

	private Test test;

	public TestPush(Test test) {
		super(Type.TEST);
		this.test = test;
		if(!StringUtils.isEmpty(test.getMessage()))
		{
			// To improve performance on JS side
			this.test.setMessage(StringUtils.substring(test.getMessage(), 0, 255) + " ...");
		}
	}

	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}
}
