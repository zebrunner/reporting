package com.qaprosoft.zafira.grid.matchers;

import com.qaprosoft.zafira.dbaccess.model.stf.STFDevice;
import com.qaprosoft.zafira.grid.models.GridRequest;

public interface IDeviceMatcher 
{
	public boolean matches(GridRequest rq, STFDevice device);
}
