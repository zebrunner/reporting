package com.qaprosoft.zafira.services.services.jmx;

import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(objectName="bean:name=rabbitMQService", description="RabbitMQ init Managed Bean",
		currencyTimeLimit=15, persistPolicy="OnUpdate", persistPeriod=200,
		persistLocation="foo", persistName="bar")
public class RabbitMQService implements IJMXService
{
	@Override
	public void init()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isConnected()
	{
		// TODO: add integration check
		return true;
	}
}