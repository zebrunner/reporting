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

import java.util.UUID;

public class AbstractPush 
{
	public enum Type
	{
		TEST_RUN, TEST, TEST_RUN_STATISTICS
	}

	private Type type;
	private String uid;

	public AbstractPush(Type type) 
	{
		this.type = type;
		this.uid = UUID.randomUUID().toString();
	}

	public Type getType() 
	{
		return type;
	}

	public void setType(Type type) 
	{
		this.type = type;
	}

	public String getUid() 
	{
		return uid;
	}

	public void setUid(String uid) 
	{
		this.uid = uid;
	}
}
