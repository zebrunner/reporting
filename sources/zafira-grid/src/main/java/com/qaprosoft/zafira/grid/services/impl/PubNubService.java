package com.qaprosoft.zafira.grid.services.impl;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.qaprosoft.zafira.dbaccess.model.Event;
import com.qaprosoft.zafira.dbaccess.model.Event.Type;
import com.qaprosoft.zafira.services.services.EventService;

@Service
public class PubNubService
{
	private Logger LOGGER = Logger.getLogger(PubNubService.class);
	
	private final String GRID_UUID = UUID.randomUUID().toString();
	
	private Pubnub pubnub;
	
	private String pKey;
	
	private String sKey;
	
	private String channel;
	
	@Autowired
	@Qualifier("messageJsonifier")
	private ObjectMapper mapper;
	
	@Autowired
	private EventService eventService;

	public PubNubService(String pKey, String sKey, String channel)
	{
		this.pKey = pKey;
		this.sKey = sKey;
		this.channel = channel;
		
		this.pubnub = new Pubnub(this.pKey, this.sKey);
		this.pubnub.setUUID(GRID_UUID);
	}
	
	public void subscribe(Callback callback) throws PubnubException
	{
		this.pubnub.subscribe(this.channel, callback);
	}
	
	public void presence(Callback callback) throws PubnubException
	{
		this.pubnub.presence(this.channel, callback);
	}
	
	/**
	 * Calls grid state and reconnects if error returned.
	 */
	public void pubNubHealthCheck()
	{
		this.pubnub.getState(this.channel, GRID_UUID, new Callback() 
		{
			@Override
			public void errorCallback(String channel, PubnubError error) 
			{
				LOGGER.info("Reconnecting PubNub due to error: " + error.getErrorString());
				pubnub.disconnectAndResubscribe();
				eventService.logEvent(new Event(Type.PUBNUB_RECONNECT, GRID_UUID));
			}
		});
	}
	
	public void publishMessage(Object message)
	{
		try
		{
			pubnub.publish(channel, new JSONObject(mapper.writeValueAsString(message)), new Callback(){});
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to publish message: " + e.getMessage());
		}
	}
}
