package com.qaprosoft.zafira.grid.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.qaprosoft.zafira.grid.queue.matchers.IDeviceMatcher;
import com.qaprosoft.zafira.grid.queue.models.GridRequest;
import com.qaprosoft.zafira.grid.queue.models.GridResponse;
import com.qaprosoft.zafira.grid.stf.STFService;
import com.qaprosoft.zafira.grid.stf.models.Device;
import com.qaprosoft.zafira.grid.stf.models.RemoteConnectUserDevice;

@Service
public class GridRequestQueueService
{
	private Logger LOGGER = Logger.getLogger(GridRequestQueueService.class);
	
	private Pubnub pubnub;
	
	private String channel;
	
	@Autowired
	@Qualifier("messageJsonifier")
	private ObjectMapper mapper;
	
	@Autowired
	private STFService stfService;
	
	@Autowired
	@Qualifier("deviceMatcher")
	private IDeviceMatcher deviceMatcher;
	
	private Map<String, GridRequest> pendingConnections = Collections.synchronizedMap(new LinkedHashMap<String, GridRequest>());
	
	private Map<String, ArrayList<String>> devicesInUse = Collections.synchronizedMap(new LinkedHashMap<String, ArrayList<String>>());
	
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
							connectDevice(rq);
							break;
						case DISCONNECT:
							disconnectDevice(rq);
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
						if(devicesInUse.containsKey(gridSessionId))
						{
							for(String serial : devicesInUse.get(gridSessionId))
							{
								stfService.disconnectDevice(serial);
							}
							devicesInUse.remove(gridSessionId);
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
	
	private synchronized void connectDevice(GridRequest rq)
	{
		boolean deviceFound = false;
		// TODO: optimize api calls for STF
		for(Device device : stfService.getAllDevices())
		{
			deviceFound = deviceFound ? true : rq.getModels().contains(device.getModel());
			
			if(!device.getPresent() || !device.getReady() || device.getUsing() || device.getOwner() != null)
			{
				continue;
			}
			
			// TODO: improve search logic
			if(deviceMatcher.matches(rq, device))
			{
				RemoteConnectUserDevice remoteDevice = stfService.connectDevice(device.getSerial());
				if(remoteDevice != null)
				{
					device.setRemoteConnectUrl(remoteDevice.getRemoteConnectUrl());
					pendingConnections.remove(rq.getTestId());
					if(!devicesInUse.containsKey(rq.getGridSessionId()))
					{
						devicesInUse.put(rq.getGridSessionId(), new ArrayList<String>());
					}
					devicesInUse.get(rq.getGridSessionId()).add(device.getSerial());
					publishMessage(new GridResponse(rq.getTestId(), device, true));
					LOGGER.info(String.format("Found device %s for test %s!", device.getModel(), rq.getTestId()));
					return;
				}
			}
		}
		if(!deviceFound)
		{
			pendingConnections.remove(rq.getTestId());
			publishMessage(new GridResponse(rq.getTestId(), false));
			LOGGER.info("Unable to find device for test: " + rq.getTestId());
		}
		else if(!pendingConnections.containsKey(rq.getTestId()))
		{
			pendingConnections.put(rq.getTestId(), rq);
		}
	}
	
	private synchronized void disconnectDevice(GridRequest rq)
	{
		stfService.disconnectDevice(rq.getSerial());
		pendingConnections.remove(rq.getTestId());
		if(devicesInUse.containsKey(rq.getGridSessionId()))
		{
			devicesInUse.get(rq.getGridSessionId()).remove(rq.getSerial());
		}
		LOGGER.info("Disconnecting device: " + rq.getSerial());
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
	
	public void processPendingConnections()
	{
		for(GridRequest rq : new LinkedHashMap<String, GridRequest>(pendingConnections).values())
		{
			connectDevice(rq);
			LOGGER.info("Processing pending device request for test: " + rq.getTestId());
		}
	}
}
