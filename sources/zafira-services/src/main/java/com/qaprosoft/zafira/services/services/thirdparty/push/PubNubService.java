package com.qaprosoft.zafira.services.services.thirdparty.push;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.qaprosoft.zafira.dbaccess.model.push.AbstractPush;

@Service
public class PubNubService implements IPushService
{
	private static final Logger logger = LoggerFactory.getLogger(PubNubService.class);

	@Autowired
	@Qualifier("messageJsonifier")
	private ObjectMapper messageJsonifier;
	
	private boolean enabled;
	private String publishKey;
	private String subscribeKey;

	private Pubnub pubnub;

	private Callback callback = new Callback()
	{
		public void successCallback(String channel, Object response)
		{
			logger.info(response.toString());
		}

		public void errorCallback(String channel, PubnubError error)
		{
			logger.error(error.toString());
		}
	};

	public PubNubService(boolean enabled, String publishKey, String subscribeKey, String adminUUID)
	{
		this.enabled = enabled;
		this.publishKey = publishKey;
		this.subscribeKey = subscribeKey;
		if(enabled) init(publishKey, subscribeKey, adminUUID);
	}
	
	private void init(String publishKey, String subscribeKey, String adminUUID)
	{
		pubnub = new Pubnub(publishKey, subscribeKey);
		pubnub.setUUID(adminUUID);
	}

	public String getPublishKey()
	{
		return publishKey;
	}

	public String getSubscribeKey()
	{
		return subscribeKey;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void publish(String channel, AbstractPush push)
	{
		if(enabled)
		{
			try
			{
				pubnub.publish(channel, new JSONObject(messageJsonifier.writeValueAsString(push)), callback);
			} catch (Exception e)
			{
				logger.error(e.getMessage());
			}
		}
	}
}
