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
package com.qaprosoft.zafira.ws.util.dozer;

import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

public class NullSafeDozerBeanMapper extends DozerBeanMapper
{
	@Override
	public <T> T map(Object source, Class<T> destinationClass) throws MappingException
	{

		return (null == source) ? null : super.map(source, destinationClass);
	}

	@Override
	public <T> T map(Object source, Class<T> destinationClass, String mapId) throws MappingException
	{
		return (null == source) ? null : super.map(source, destinationClass, mapId);
	}

	@Override
	public void map(Object source, Object destination) throws MappingException
	{
		if (null != source)
		{
			super.map(source, destination);
		}
	}

	@Override
	public void map(Object source, Object destination, String mapId) throws MappingException
	{
		if (null != source)
		{
			super.map(source, destination, mapId);
		}
	}
}
