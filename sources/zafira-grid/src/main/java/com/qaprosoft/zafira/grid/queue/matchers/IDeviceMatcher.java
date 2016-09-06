package com.qaprosoft.zafira.grid.queue.matchers;

import com.qaprosoft.zafira.grid.queue.models.GridRequest;
import com.qaprosoft.zafira.grid.stf.models.Device;

public interface IDeviceMatcher 
{
	public boolean matches(GridRequest rq, Device device);
}
