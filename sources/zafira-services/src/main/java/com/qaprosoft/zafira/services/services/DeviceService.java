package com.qaprosoft.zafira.services.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.DeviceMapper;
import com.qaprosoft.zafira.models.db.Device;
import com.qaprosoft.zafira.models.stf.STFDevice;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.stf.STFService;

@Service
public class DeviceService
{
	@Autowired
	private DeviceMapper deviceMapper;
	
	@Autowired
	private STFService stfService;

	@Transactional(readOnly = true)
	public List<Device> getAllDevices() throws ServiceException
	{
		return deviceMapper.getAllDevices();
	}

	@Transactional(rollbackFor = Exception.class)
	public Device updateDevice(Device device) throws ServiceException
	{
		deviceMapper.updateDevice(device);
		return device;
	}

	@Transactional(rollbackFor = Exception.class)
	public Device createDevice(Device device) throws ServiceException
	{
		deviceMapper.createDevice(device);
		return device;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteDeviceById(Long id) throws ServiceException
	{
		deviceMapper.deleteDeviceById(id);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void syncDevicesWithSTF() throws ServiceException
	{
		Map<String, STFDevice> stfDevices = stfService.getAllDevicesAsMap();
		for(Device device : getAllDevices())
		{
			if(stfDevices.containsKey(device.getSerial()))
			{
				STFDevice stfDevice = stfDevices.get(device.getSerial());
				device.setEnabled(true);
				device.setLastStatus(stfDevice.getReady() && stfDevice.getPresent());
				updateDevice(device);
				stfDevices.remove(device.getSerial());
			}
			else
			{
				deleteDeviceById(device.getId());
			}
		}
		for(String serial : stfDevices.keySet())
		{	
			STFDevice stfDevice = stfDevices.get(serial);
			Device device = new Device();
			device.setModel(stfDevice.getModel());
			device.setSerial(stfDevice.getSerial());
			device.setEnabled(true);
			device.setLastStatus(stfDevice.getReady() && stfDevice.getPresent());
			createDevice(device);
		}
	}
}
