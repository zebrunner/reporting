package com.qaprosoft.zafira.grid.queue.matchers;

import com.qaprosoft.zafira.dbaccess.model.stf.STFDevice;
import com.qaprosoft.zafira.grid.queue.models.GridRequest;

public class DeviceByModelMatcher implements IDeviceMatcher
{
	@Override
	public boolean matches(GridRequest rq, STFDevice device) 
	{
		return rq.getModels().contains(device.getModel().trim());
	}
}
