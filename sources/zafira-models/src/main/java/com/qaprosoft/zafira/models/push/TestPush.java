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
package com.qaprosoft.zafira.models.push;

import com.qaprosoft.zafira.models.db.application.Test;

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
