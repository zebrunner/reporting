package com.qaprosoft.zafira.dbaccess.dao.mysql;

import java.util.List;

import com.qaprosoft.zafira.models.db.Device;

public interface DeviceMapper
{
	void createDevice(Device device);

	Device getDeviceById(long id);

	List<Device> getAllDevices();

	void updateDevice(Device device);

	void deleteDevice(Device device);

	void deleteDeviceById(long id);
}
