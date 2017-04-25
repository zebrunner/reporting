package com.qaprosoft.zafira.models.db.ua;

import com.qaprosoft.zafira.models.db.AbstractEntity;

public class UAInspection extends AbstractEntity
{
	private static final long serialVersionUID = -6411176780323834507L;

	private String systemID;
	private String serialNumber;
	private String firmwareRev;
	private String hardwareRev;
	private int batteryLevel;

	public String getSystemID()
	{
		return systemID;
	}

	public void setSystemID(String systemID)
	{
		this.systemID = systemID;
	}

	public String getSerialNumber()
	{
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber)
	{
		this.serialNumber = serialNumber;
	}

	public String getFirmwareRev()
	{
		return firmwareRev;
	}

	public void setFirmwareRev(String firmwareRev)
	{
		this.firmwareRev = firmwareRev;
	}

	public String getHardwareRev()
	{
		return hardwareRev;
	}

	public void setHardwareRev(String hardwareRev)
	{
		this.hardwareRev = hardwareRev;
	}

	public int getBatteryLevel()
	{
		return batteryLevel;
	}

	public void setBatteryLevel(int batteryLevel)
	{
		this.batteryLevel = batteryLevel;
	}
}