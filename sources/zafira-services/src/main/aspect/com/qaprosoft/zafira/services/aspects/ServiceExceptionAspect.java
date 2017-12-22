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
package com.qaprosoft.zafira.services.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Aspect
public class ServiceExceptionAspect
{
	private static final Logger logger = LoggerFactory.getLogger(ServiceExceptionAspect.class);

	@AfterThrowing(pointcut = "execution(public * com.qaprosoft.zafira.services.services..*.*(..) throws com.qaprosoft.zafira.services.exceptions.ServiceException)", throwing = "e")
	public void rethrowException(JoinPoint joinPoint, Exception e) throws ServiceException
	{
		logger.error("Got exception when calling [{}]", joinPoint.getSignature().toString(), e);

		if (e instanceof ServiceException)
		{
			throw (ServiceException) e;
		} else
		{
			throw new ServiceException(e);
		}
	}
}
