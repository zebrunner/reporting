package com.qaprosoft.zafira.grid.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.qaprosoft.zafira.grid.queue.GridRequestQueueService;
import com.qaprosoft.zafira.grid.queue.models.GridRequest;
import com.qaprosoft.zafira.grid.stf.STFService;
import com.qaprosoft.zafira.grid.stf.models.Device;
import com.qaprosoft.zafira.grid.stf.models.RemoteConnectUserDevice;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

public class GridRequestQueueProcessorTask 
{	
	@Value("${zafira.grid.device_timeout.sec}")
	private long deviceTimeout;
	
	@Autowired
	private GridRequestQueueService gridRequestQueueService;
	
	@Autowired
	private STFService stfService;
	
	public void runTask() throws ServiceException
	{
		// Process disconnect device requests
		for(GridRequest rq : gridRequestQueueService.getDisconnectRequests())
		{
			stfService.disconnectDevice(rq.getSerial());
		}
		gridRequestQueueService.getDisconnectRequests().clear();
		
		// Process connect device requests
		for(GridRequest rq : gridRequestQueueService.getConnectRequests().values())
		{
			boolean deviceFound = false;
			for(Device device : stfService.getAllDevices())
			{
				deviceFound = deviceFound ? true : rq.getModels().contains(device.getModel());
				
				if(!device.getPresent() || !device.getReady() || device.getUsing() || device.getOwner() != null)
				{
					continue;
				}
				
				// TODO: improve search logic
				if(rq.getModels().contains(device.getModel()))
				{
					RemoteConnectUserDevice remoteDevice = stfService.connectDevice(device.getSerial(), deviceTimeout);
					if(remoteDevice != null)
					{
						device.setRemoteConnectUrl(remoteDevice.getRemoteConnectUrl());
						gridRequestQueueService.notifyDeviceConnected(rq.getTestId(), device);
						deviceFound = true;
						break;
					}
				}
			}
			if(!deviceFound)
			{
				gridRequestQueueService.notifyDeviceNotConnected(rq.getTestId());
			}
		}
	}
}
