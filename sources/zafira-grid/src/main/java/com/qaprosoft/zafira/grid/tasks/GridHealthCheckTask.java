package com.qaprosoft.zafira.grid.tasks;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.qaprosoft.zafira.models.db.Device;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.stf.STFDevice;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.DeviceService;
import com.qaprosoft.zafira.services.services.EmailService;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.services.services.emails.GridStatusEmail;
import com.qaprosoft.zafira.services.services.stf.STFService;

public class GridHealthCheckTask 
{	
	@Autowired
	private STFService stfService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private SettingsService settingsService;
	
	public void runTask() throws ServiceException
	{
		boolean statusChanged = false;
		List<Device> devices = deviceService.getAllDevices();
		Map<String, STFDevice> stfDevices = stfService.getAllDevicesAsMap();
		for(Device device : devices)
		{
			if(device.isEnabled())
			{
				boolean currentDeviceStatus = false;
				if(stfDevices.containsKey(device.getSerial()))
				{
					STFDevice stfDevice = stfDevices.get(device.getSerial());
					currentDeviceStatus = stfDevice.getPresent() && stfDevice.getReady();
				}
				if(currentDeviceStatus != device.isLastStatus())
				{
					device.setLastStatus(currentDeviceStatus);
					if(!currentDeviceStatus)
					{
						device.setDisconnects(device.getDisconnects() + 1);
					}
					device.setStatusChanged(true);
					deviceService.updateDevice(device);
					statusChanged = true;
				}
			}
		}
		if(statusChanged)
		{
			Setting setting = settingsService.getSettingByType(Setting.SettingType.STF_NOTIFICATION_RECIPIENTS);
			if(setting != null && setting.getValue() != null && !setting.getValue().isEmpty())
			{
				String [] receipients = setting.getValue().trim().replaceAll(",", " ").replaceAll(";", " ").split(" ");
				emailService.sendEmail(new GridStatusEmail(devices), receipients);		
			}
		}
	}
}
