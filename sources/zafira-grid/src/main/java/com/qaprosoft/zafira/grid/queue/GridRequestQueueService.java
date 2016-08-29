package com.qaprosoft.zafira.grid.queue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.qaprosoft.zafira.grid.queue.models.GridRequest;
import com.qaprosoft.zafira.grid.queue.models.GridResponse;
import com.qaprosoft.zafira.grid.stf.models.Device;

@Service
public class GridRequestQueueService
{
	private Logger LOGGER = Logger.getLogger(GridRequestQueueService.class);
	
	private Pubnub pubnub;
	
	private String channel;
	
	@Autowired
	@Qualifier("messageJsonifier")
	private ObjectMapper mapper;
	
	private Map<String, GridRequest> connectRequests = Collections.synchronizedMap(new LinkedHashMap<String, GridRequest>());
	
	private Set<GridRequest> disconnectRequests = Collections.synchronizedSet(new LinkedHashSet<GridRequest>());
	
	public GridRequestQueueService(String pubKey, String subKey, String channel) throws PubnubException
	{
		this.pubnub = new Pubnub(pubKey, subKey);
		this.channel = channel;
		this.pubnub.subscribe(channel, new Callback()
		{
			@Override
			public void successCallback(String channel, Object message)
			{
				try
				{
					String json = ((JSONObject) message).toString(); 
					if(json.contains("operation"))
					{
						GridRequest rq = mapper.readValue(json, GridRequest.class);
						switch (rq.getOperation())
						{
						case CONNECT:
							connectRequests.put(rq.getTestId(), rq);
							break;
						case DISCONNECT:
							connectRequests.remove(rq.getTestId());
							disconnectRequests.add(rq);
							break;	
						}
					}
				}
				catch (Exception e)
				{
					LOGGER.error(e.getMessage());
				}
			}
		});
	}
	
	public void notifyDeviceConnected(String testId, Device device)
	{
		connectRequests.remove(testId);
		publishMessage(new GridResponse(testId, device, true));
	}
	
	public void notifyDeviceNotConnected(String testId)
	{
		connectRequests.remove(testId);
		publishMessage(new GridResponse(testId, false));
	}
	
	private void publishMessage(GridResponse rs)
	{
		try
		{
			pubnub.publish(channel, new JSONObject(mapper.writeValueAsString(rs)), new Callback(){});
		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
	}

	public Map<String, GridRequest> getConnectRequests()
	{
		return connectRequests;
	}

	public Set<GridRequest> getDisconnectRequests()
	{
		return disconnectRequests;
	}
}
