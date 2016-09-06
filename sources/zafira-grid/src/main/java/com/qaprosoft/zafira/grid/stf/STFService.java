package com.qaprosoft.zafira.grid.stf;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.grid.stf.models.Device;
import com.qaprosoft.zafira.grid.stf.models.RemoteConnectUserDevice;

@Service
public class STFService
{
	@Value("${zafira.grid.device_timeout.sec}")
	private long deviceTimeout;
	
	private STFClient stfClient;

	public STFService(String serviceURL, String authToken)
	{
		this.stfClient = new STFClient(serviceURL, authToken);
	}
	
	@Transactional
	public List<Device> getAllDevices()
	{
		return stfClient.getAllDevices().getObject().getDevices();
	}
	
	@Transactional
	public RemoteConnectUserDevice connectDevice(String serial)
	{
		RemoteConnectUserDevice device = null;
		if(stfClient.reserveDevice(serial, deviceTimeout * 1000))
		{
			device = stfClient.remoteConnectDevice(serial).getObject();
		}
		return device;
	}
	
	@Transactional
	public boolean disconnectDevice(String serial)
	{
		return stfClient.remoteDisconnectDevice(serial) && stfClient.returnDevice(serial);
	}
	
}
