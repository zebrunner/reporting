package com.qaprosoft.zafira.grid.queue.matchers;

import com.qaprosoft.zafira.grid.queue.models.GridRequest;
import com.qaprosoft.zafira.grid.stf.models.Device;

public class DeviceByModelMatcher implements IDeviceMatcher
{
	@Override
	public boolean matches(GridRequest rq, Device device) 
	{
		return rq.getModels().contains(device.getModel().trim());
	}
}
