package com.qaprosoft.zafira.grid.matchers;

import com.qaprosoft.zafira.grid.models.GridRequest;
import com.qaprosoft.zafira.models.stf.STFDevice;

public interface IDeviceMatcher 
{
	public boolean matches(GridRequest rq, STFDevice device);
}
