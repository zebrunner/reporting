package com.qaprosoft.zafira.grid.matchers;

import com.qaprosoft.zafira.grid.models.GridRequest;
import com.qaprosoft.zafira.models.stf.STFDevice;

public class DeviceByModelMatcher implements IDeviceMatcher
{
	@Override
	public boolean matches(GridRequest rq, STFDevice device) 
	{
		return rq.getModels().contains(device.getModel().trim());
	}
}
