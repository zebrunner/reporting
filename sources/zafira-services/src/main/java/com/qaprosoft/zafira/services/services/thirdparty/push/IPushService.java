package com.qaprosoft.zafira.services.services.thirdparty.push;

import com.qaprosoft.zafira.dbaccess.model.push.AbstractPush;

public interface IPushService
{
	public void publish(String channel, AbstractPush push);
}
