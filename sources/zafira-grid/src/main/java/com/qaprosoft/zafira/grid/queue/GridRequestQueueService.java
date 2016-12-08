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
import com.google.gson.Gson;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.qaprosoft.zafira.dbaccess.model.Event;
import com.qaprosoft.zafira.dbaccess.model.Event.Type;
import com.qaprosoft.zafira.dbaccess.model.stf.RemoteConnectUserDevice;
import com.qaprosoft.zafira.dbaccess.model.stf.STFDevice;
import com.qaprosoft.zafira.grid.queue.matchers.IDeviceMatcher;
import com.qaprosoft.zafira.grid.queue.models.GridRequest;
import com.qaprosoft.zafira.grid.queue.models.GridResponse;
import com.qaprosoft.zafira.services.services.EventService;
import com.qaprosoft.zafira.services.services.stf.STFService;

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
	private EventService eventService;
	
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
							eventService.markEventReceived(Type.REQUEST_DEVICE_CONNECT, rq.getGridSessionId(), rq.getTestId());
							break;
						case DISCONNECT:
							disconnectDevice(rq);
							eventService.markEventReceived(Type.REQUEST_DEVICE_DISCONNECT, rq.getGridSessionId(), rq.getTestId());
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
							eventService.logEvent(new Event(Type.HEARTBEAT_TIMEOUT, gridSessionId));
							LOGGER.info("Disconnecting devices by timeout for suite: " + gridSessionId);
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
		if(pendingConnections.containsKey(rq.getTestId()))
		{
			boolean deviceFound = false;
			// TODO: optimize api calls for STF
			for(STFDevice device : stfService.getAllDevices())
			{
				deviceFound = deviceFound ? true : rq.getModels().contains(device.getModel()) && device.getReady() && device.getPresent();
				
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
						GridResponse rs = new GridResponse(rq.getTestId(), device, true);
						publishMessage(rs);
						eventService.logEvent(new Event(Type.CONNECT_DEVICE, rq.getGridSessionId(), rq.getTestId(), new Gson().toJson(rs)));
						LOGGER.info(String.format("Found device %s for test %s.", device.getSerial(), rq.getTestId()));
						return;
					}
				}
			}
			if(!deviceFound)
			{
				pendingConnections.remove(rq.getTestId());
				GridResponse rs = new GridResponse(rq.getTestId(), false);
				publishMessage(rs);
				eventService.logEvent(new Event(Type.DEVICE_NOT_FOUND, rq.getGridSessionId(), rq.getTestId(), new Gson().toJson(rs)));
				LOGGER.info("Unable to find device for test: " + rq.getTestId());
			}
		}
		else
		{
			pendingConnections.put(rq.getTestId(), rq);
		}
	}
	
	private synchronized void disconnectDevice(GridRequest rq)
	{
		if(!pendingConnections.containsKey(rq.getTestId()))
		{
			stfService.disconnectDevice(rq.getSerial());
			if(devicesInUse.containsKey(rq.getGridSessionId()))
			{
				devicesInUse.get(rq.getGridSessionId()).remove(rq.getSerial());
			}
			eventService.logEvent(new Event(Type.DISCONNECT_DEVICE, rq.getGridSessionId(), rq.getTestId(), new Gson().toJson(rq)));
			LOGGER.info(String.format("Disconnecting device %s from test %s.", rq.getSerial(), rq.getTestId()));
		}
		else
		{
			pendingConnections.remove(rq.getTestId());
		}
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
		LOGGER.info(String.format("Starting to process %d device requests...", pendingConnections.size()));
		for(GridRequest rq : new LinkedHashMap<String, GridRequest>(pendingConnections).values())
		{
			connectDevice(rq);
		}
	}
}
