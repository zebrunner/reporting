package com.qaprosoft.zafira.grid.queue.matchers;

import com.qaprosoft.zafira.dbaccess.model.stf.STFDevice;
import com.qaprosoft.zafira.grid.queue.models.GridRequest;

public interface IDeviceMatcher 
{
	public boolean matches(GridRequest rq, STFDevice device);
}
