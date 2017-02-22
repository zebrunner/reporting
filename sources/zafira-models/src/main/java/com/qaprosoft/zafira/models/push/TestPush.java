package com.qaprosoft.zafira.models.push;

import com.qaprosoft.zafira.models.db.Test;

public class TestPush extends AbstractPush {

	private Test test;

	public TestPush(Test test) {
		super(Type.TEST);
		this.test = test;
		if(test.getMessage() != null && !test.getMessage().isEmpty())
		{
			// To improve performance on JS side
			String message = test.getMessage();
			message = message.length() > 255 ? message.substring(0, 255) : message;
			this.test.setMessage(message + " ...");
		}
	}

	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}
}
