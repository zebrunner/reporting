package com.zafira.ws.util.dozer;

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
