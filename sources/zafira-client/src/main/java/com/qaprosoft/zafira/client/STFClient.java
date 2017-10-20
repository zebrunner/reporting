package com.qaprosoft.zafira.client;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qaprosoft.zafira.models.stf.Devices;
import com.qaprosoft.zafira.models.stf.RemoteConnectUserDevice;
import com.qaprosoft.zafira.models.stf.Response;
import com.qaprosoft.zafira.models.stf.STFDevice;
import com.sun.jersey.api.client.Client;

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

	public Response<Devices> getAllDevices()
	{
		Response<Devices> result = new Response<Devices>(0, null);
		try
		{
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(serviceURL + DEVICES_PATH);
			request.addHeader("Authorization", "Bearer " + authToken); // header
			HttpResponse response = client.execute(request);

			int status = response.getStatusLine().getStatusCode();
			if (status == 200)
			{
				ObjectMapper mapper = new ObjectMapper();
				Devices devices = mapper.readValue(response.getEntity().getContent(), Devices.class); // object
				result.setStatus(status);
				result.setObject(devices);
			}
		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}

		return result;
	}

	public Response<STFDevice> getDevice(String udid)
	{
		Response<STFDevice> result = new Response<STFDevice>(0, null);
		try
		{
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(serviceURL + DEVICES_PATH + "/" + udid);
			request.addHeader("Authorization", "Bearer " + authToken); // header
			HttpResponse response = client.execute(request);

			int status = response.getStatusLine().getStatusCode();
			if (status == 200)
			{
				ObjectMapper mapper = new ObjectMapper();
				JSONObject json = new JSONObject(IOUtils.toString(response.getEntity().getContent()));
				STFDevice device = mapper.readValue(json.getString("device"), STFDevice.class);
				result.setStatus(status);
				result.setObject(device);
			}
		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}

		return result;
	}

	public boolean reserveDevice(String serial, long timeout)
	{
		boolean isSuccess = false;
		try
		{
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceURL + USER_DEVICES_PATH);
			request.addHeader("Authorization", "Bearer " + authToken); // header

			StringEntity entity = new StringEntity("{\"serial\":\"" + serial + "\"}");
			entity.setContentType("application/json");
			request.setEntity(entity);

			HttpResponse response = client.execute(request);

			isSuccess = response.getStatusLine().getStatusCode() == 200;

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
		}

		return isSuccess;
	}

	public boolean returnDevice(String serial)
	{
		boolean isSuccess = false;
		try
		{
			HttpClient client = HttpClientBuilder.create().build();
			HttpDelete request = new HttpDelete(serviceURL + String.format(USER_DEVICES_BY_ID_PATH, serial));
			request.addHeader("Authorization", "Bearer " + authToken); // header
			HttpResponse response = client.execute(request);

			isSuccess = response.getStatusLine().getStatusCode() == 200;

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
		}

		return isSuccess;
	}

	public Response<RemoteConnectUserDevice> remoteConnectDevice(String serial)
	{
		Response<RemoteConnectUserDevice> result = new Response<RemoteConnectUserDevice>(0, null);
		try
		{
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceURL + String.format(USER_DEVICES_REMOTE_CONNECT_PATH, serial));
			request.addHeader("Authorization", "Bearer " + authToken); // header
			HttpResponse response = client.execute(request);

			int status = response.getStatusLine().getStatusCode();
			if (status == 200)
			{
				ObjectMapper mapper = new ObjectMapper();
				RemoteConnectUserDevice devices = mapper.readValue(response.getEntity().getContent(),
						RemoteConnectUserDevice.class); // object
				result.setStatus(status);
				result.setObject(devices);
			}
		} catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
		}

		return result;
	}

	public boolean remoteDisconnectDevice(String serial)
	{
		boolean isSuccess = false;
		try
		{
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceURL + String.format(USER_DEVICES_REMOTE_CONNECT_PATH, serial));
			request.addHeader("Authorization", "Bearer " + authToken); // header
			HttpResponse response = client.execute(request);

			isSuccess = response.getStatusLine().getStatusCode() == 200;

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
		}

		return isSuccess;
	}

	public boolean isConnected() {
		boolean isSuccess = false;
		try
		{
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(serviceURL + DEVICES_PATH);
			request.addHeader("Authorization", "Bearer " + authToken); // header
			HttpResponse response = client.execute(request);

			isSuccess = response.getStatusLine().getStatusCode() == 200;

		} catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
		}
		return isSuccess;
	}
}