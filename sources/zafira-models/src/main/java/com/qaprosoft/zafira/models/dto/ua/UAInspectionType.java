package com.qaprosoft.zafira.models.dto.ua;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.dto.AbstractType;

@JsonInclude(Include.NON_NULL)
public class UAInspectionType extends AbstractType
{
	private static final long serialVersionUID = 4040776018404508834L;

	@NotNull
	private String systemID;
	@NotNull
	private String serialNumber;
	@NotNull
	private String firmwareRev;
	@NotNull
	private String hardwareRev;
	@NotNull
	private int batteryLevel;
	
	public UAInspectionType()
	{
	}
	
	public UAInspectionType(String systemID, String serialNumber, String firmwareRev, String hardwareRev, int batteryLevel)
	{
		super();
		this.systemID = systemID;
		this.serialNumber = serialNumber;
		this.firmwareRev = firmwareRev;
		this.hardwareRev = hardwareRev;
		this.batteryLevel = batteryLevel;
	}

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