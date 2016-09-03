package com.qaprosoft.zafira.grid.queue;

import java.util.ArrayList;
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
	
	private Map<String, ArrayList<String>> busyDevices = Collections.synchronizedMap(new LinkedHashMap<String, ArrayList<String>>());
	
	public GridRequestQueueService(String pubKey, String subKey, String channel) throws PubnubException
	{
		this.pubnub = new Pubnub(pubKey, subKey);
		this.channel = channel;
		// Device connect / disconnect handling
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
							busyDevices.get(rq.getGridSessionId()).remove(rq.getSerial());
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
		// Session timeout handling - when test suites aborted
		this.pubnub.presence(channel, new Callback() 
		{
			@Override
			public void successCallback(String channel, Object message) 
			{
				try
				{
					JSONObject json = (JSONObject) message;
					if("timeout".equals(json.getString("action")))
					{
						String gridSessionId = json.getString("uuid");
						if(busyDevices.containsKey(gridSessionId))
						{
							for(String serial : busyDevices.get(gridSessionId))
							{
								disconnectRequests.add(new GridRequest(serial));
							}
							busyDevices.remove(gridSessionId);
							LOGGER.info("Disconnecting devices by timeout fot grid session: " + gridSessionId);
						}
					}
				}
				catch(Exception e)
				{
					LOGGER.error(e.getMessage());
				}
			}
		});
	}
	
	public void notifyDeviceConnected(GridRequest rq, Device device)
	{
		connectRequests.remove(rq.getTestId());
		if(!busyDevices.containsKey(rq.getGridSessionId()))
		{
			busyDevices.put(rq.getGridSessionId(), new ArrayList<String>());
		}
		busyDevices.get(rq.getGridSessionId()).add(device.getSerial());
		publishMessage(new GridResponse(rq.getTestId(), device, true));
	}
	
	public void notifyDeviceNotConnected(GridRequest rq)
	{
		connectRequests.remove(rq.getTestId());
		publishMessage(new GridResponse(rq.getTestId(), false));
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
