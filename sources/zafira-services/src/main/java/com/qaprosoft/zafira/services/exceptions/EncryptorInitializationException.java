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
package com.qaprosoft.zafira.services.exceptions;

/**
 * Created by irina on 18.8.17.
 */
public class EncryptorInitializationException extends ServiceException
{
	private static final long serialVersionUID = 1272617468905992114L;

	public EncryptorInitializationException()
	{
		super();
	}

	public EncryptorInitializationException(String message)
	{
		super(message);
	}

	public EncryptorInitializationException(Throwable cause)
	{
		super(cause);
	}

	public EncryptorInitializationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
