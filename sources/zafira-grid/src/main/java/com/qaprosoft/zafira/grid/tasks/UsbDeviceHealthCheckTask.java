package com.qaprosoft.zafira.grid.tasks;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.qaprosoft.zafira.dbaccess.model.stf.STFDevice;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.stf.STFService;

/**
 * Job calls STF to get devices status trying to reset device USB if it is disconnected.
 * 
 * @author akhursevich
 */
public class UsbDeviceHealthCheckTask 
{	
	private static Logger LOGGER = Logger.getLogger(UsbDeviceHealthCheckTask.class);

	@Autowired
	private STFService stfService;
	
	private String sshPath;
	
	public UsbDeviceHealthCheckTask() throws URISyntaxException
	{
		try
		{
			URL resource = UsbDeviceHealthCheckTask.class.getResource("/usbreset.sh");
			this.sshPath = Paths.get(resource.toURI()).toString();
		}
		catch(Exception e)
		{
			LOGGER.error("Unable to load usbreset.sh: " + e.getMessage());
		}
	}
	
	public void runTask() throws ServiceException
	{
		List<STFDevice> devices = stfService.getAllDevices();
		for(STFDevice device : devices)
		{
			if(!(device.getPresent() && device.getReady()))
			{
				LOGGER.info(device.getModel() + " - disconnected, trying to reset USB...");
				try
				{
					ProcessBuilder pb = new ProcessBuilder(this.sshPath, device.getSerial());
					pb.start().waitFor();
				}
				catch(Exception e)
				{
					LOGGER.error("Unable to run usbreset.sh: " + e.getMessage(), e);
				}
			}
		}
	}
}
