package com.qaprosoft.zafira.services.services.stf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.model.stf.RemoteConnectUserDevice;
import com.qaprosoft.zafira.dbaccess.model.stf.STFDevice;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service("stfService")
public class STFService
{
	private STFClient stfClient;

	public STFService(String serviceURL, String authToken)
	{
		this.stfClient = new STFClient(serviceURL, authToken);
	}
	
	@Transactional
	public List<STFDevice> getAllDevices()
	{
		return stfClient.getAllDevices().getObject().getDevices();
	}
	
	@Transactional(readOnly = true)
	public Map<String, STFDevice> getAllDevicesAsMap() throws ServiceException
	{
		Map<String, STFDevice> devices = new HashMap<>();
		for(STFDevice device : getAllDevices())
		{
			devices.put(device.getSerial(), device);
		}
		return devices;
	}
	
	@Transactional
	public RemoteConnectUserDevice connectDevice(String serial, long connectTimeoutSec)
	{
		RemoteConnectUserDevice device = null;
		if(stfClient.reserveDevice(serial,  TimeUnit.SECONDS.toMillis(connectTimeoutSec)))
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
