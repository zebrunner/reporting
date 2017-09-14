package com.qaprosoft.zafira.client;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qaprosoft.zafira.models.stf.Devices;
import com.qaprosoft.zafira.models.stf.RemoteConnectUserDevice;
import com.qaprosoft.zafira.models.stf.Response;
import com.qaprosoft.zafira.models.stf.Serial;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class STFClient
{
	private static final Logger LOGGER = LoggerFactory.getLogger(STFClient.class);
	
	// Max device timeout 1 hour
	private static final Integer TIMEOUT = 60 * 60 * 1000;
	
	private static final String DEVICES_PATH = "/api/v1/devices";
	private static final String USER_DEVICES_PATH = "/api/v1/user/devices";
	private static final String USER_DEVICES_BY_ID_PATH = "/api/v1/user/devices/%s";
	private static final String USER_DEVICES_REMOTE_CONNECT_PATH = "/api/v1/user/devices/%s/remoteConnect";
	
	private Client client;
	private String serviceURL;
	private String authToken;
	
	public STFClient(String serviceURL, String authToken)
	{
		this.serviceURL = serviceURL;
		this.authToken = authToken;
		
		this.client = Client.create();
		this.client.setConnectTimeout(TIMEOUT);
		this.client.setReadTimeout(TIMEOUT);
	}
	
	public synchronized Response<Devices> getAllDevices()
	{
		Response<Devices> response = new Response<Devices>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + DEVICES_PATH);
			ClientResponse clientRS =  initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			response.setStatus(clientRS.getStatus());
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(Devices.class));
			}

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return response;
	}
	
	public synchronized boolean reserveDevice(String serial, long timeout)
	{
		boolean isSuccess = false;
		try
		{
			WebResource webResource = client.resource(serviceURL + USER_DEVICES_PATH);
			ClientResponse clientRS =  initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, new Serial(serial, timeout));
			isSuccess = clientRS.getStatus() == 200 ? true : false;

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return isSuccess;
	}
	
	public synchronized boolean returnDevice(String serial)
	{
		boolean isSuccess = false;
		try
		{
			WebResource webResource = client.resource(serviceURL + String.format(USER_DEVICES_BY_ID_PATH, serial));
			ClientResponse clientRS =  initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
			isSuccess = clientRS.getStatus() == 200 ? true : false;

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return isSuccess;
	}
	
	public synchronized Response<RemoteConnectUserDevice> remoteConnectDevice(String serial)
	{
		Response<RemoteConnectUserDevice> response = new Response<RemoteConnectUserDevice>(0, null);
		try
		{
			WebResource webResource = client.resource(serviceURL + String.format(USER_DEVICES_REMOTE_CONNECT_PATH, serial));
			ClientResponse clientRS =  initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
			if (clientRS.getStatus() == 200)
			{
				response.setObject(clientRS.getEntity(RemoteConnectUserDevice.class));
			}

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return response;
	}
	
	public synchronized boolean remoteDisconnectDevice(String serial)
	{
		boolean isSuccess = false;
		try
		{
			WebResource webResource = client.resource(serviceURL + String.format(USER_DEVICES_REMOTE_CONNECT_PATH, serial));
			ClientResponse clientRS =  initHeaders(webResource.type(MediaType.APPLICATION_JSON))
					.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
			isSuccess = clientRS.getStatus() == 200 ? true : false;

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return isSuccess;
	}
	
	private WebResource.Builder initHeaders(WebResource.Builder builder)
	{
		if(!StringUtils.isEmpty(authToken))
		{
			builder.header("Authorization", "Bearer " + authToken);
		}
		return builder;
	}
}