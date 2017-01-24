package com.qaprosoft.zafira.grid.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubException;
import com.qaprosoft.zafira.models.db.Event;
import com.qaprosoft.zafira.models.db.Event.Type;
import com.qaprosoft.zafira.dbaccess.model.stf.RemoteConnectUserDevice;
import com.qaprosoft.zafira.dbaccess.model.stf.STFDevice;
import com.qaprosoft.zafira.grid.matchers.IDeviceMatcher;
import com.qaprosoft.zafira.grid.models.GridRequest;
import com.qaprosoft.zafira.grid.models.GridResponse;
import com.qaprosoft.zafira.services.services.EventService;
import com.qaprosoft.zafira.services.services.stf.STFService;

@Service
public class GridRequestQueueService
{
	private Logger LOGGER = Logger.getLogger(GridRequestQueueService.class);
	
	@Value("${zafira.grid.device_timeout.sec}")
	private long connectTimeoutSec;
	
	@Value("${zafira.grid.pending_timeout.sec}")
	private long pendingTimeoutSec;
	
	@Autowired
	@Qualifier("messageJsonifier")
	private ObjectMapper mapper;
	
	@Autowired
	private STFService stfService;
	
	@Autowired
	private EventService eventService;
	
	private PubNubService pubNubService;
	
	private IDeviceMatcher deviceMatcher;
	
	private Map<String, GridRequest> pendingConnections;
	
	private Map<String, ArrayList<String>> devicesInUse;
	
	public GridRequestQueueService(PubNubService pubNubService, IDeviceMatcher deviceMatcher)
	{
		this.pubNubService = pubNubService;
		this.deviceMatcher = deviceMatcher;
	}
	
	@PostConstruct
	public void init() throws PubnubException
	{
		this.pendingConnections = new PassiveExpiringMap<String, GridRequest>(TimeUnit.SECONDS.toMillis(pendingTimeoutSec), Collections.synchronizedMap(new LinkedHashMap<String, GridRequest>()));
		this.devicesInUse = Collections.synchronizedMap(new LinkedHashMap<String, ArrayList<String>>());
	
		// Device connect / disconnect handling
		this.pubNubService.subscribe(new Callback()
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
							pendingConnections.put(rq.getTestId(), rq);
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
		
		// Session timeout handling - when test run is aborted
		this.pubNubService.presence(new Callback() 
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
	
	public synchronized void connectDevice(GridRequest rq)
	{
		if(this.pendingConnections.containsKey(rq.getTestId()))
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
				if(this.deviceMatcher.matches(rq, device))
				{
					RemoteConnectUserDevice remoteDevice = stfService.connectDevice(device.getSerial(), TimeUnit.SECONDS.toMillis(connectTimeoutSec));
					if(remoteDevice != null)
					{
						device.setRemoteConnectUrl(remoteDevice.getRemoteConnectUrl());
						this.pendingConnections.remove(rq.getTestId());
						if(!this.devicesInUse.containsKey(rq.getGridSessionId()))
						{
							this.devicesInUse.put(rq.getGridSessionId(), new ArrayList<String>());
						}
						this.devicesInUse.get(rq.getGridSessionId()).add(device.getSerial());
						GridResponse rs = new GridResponse(rq.getTestId(), device, true);
						this.pubNubService.publishMessage(rs);
						eventService.logEvent(new Event(Type.CONNECT_DEVICE, rq.getGridSessionId(), rq.getTestId(), new Gson().toJson(rs)));
						LOGGER.info(String.format("Found device %s for test %s.", device.getSerial(), rq.getTestId()));
						return;
					}
				}
			}
			if(!deviceFound)
			{
				this.pendingConnections.remove(rq.getTestId());
				GridResponse rs = new GridResponse(rq.getTestId(), false);
				this.pubNubService.publishMessage(rs);
				eventService.logEvent(new Event(Type.DEVICE_NOT_FOUND, rq.getGridSessionId(), rq.getTestId(), new Gson().toJson(rs)));
				LOGGER.info("Unable to find device for test: " + rq.getTestId());
			}
		}
	}
	
	public synchronized void disconnectDevice(GridRequest rq)
	{
		stfService.disconnectDevice(rq.getSerial());
		if(this.devicesInUse.containsKey(rq.getGridSessionId()))
		{
			this.devicesInUse.get(rq.getGridSessionId()).remove(rq.getSerial());
		}
		this.pendingConnections.remove(rq.getTestId());
		eventService.logEvent(new Event(Type.DISCONNECT_DEVICE, rq.getGridSessionId(), rq.getTestId(), new Gson().toJson(rq)));
		LOGGER.info(String.format("Disconnecting device %s from test %s.", rq.getSerial(), rq.getTestId()));
	}
	
	public Collection<GridRequest> getPendingConnectionRequests()
	{
		return new LinkedHashMap<String, GridRequest>(pendingConnections).values();
	}
}
