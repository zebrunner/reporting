package com.qaprosoft.zafira.grid.tasks;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import com.qaprosoft.zafira.models.stf.STFDevice;
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
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	private String sshPath;
	
	@PostConstruct
	public void init() 
	{
		InputStream is = null;
		try
		{
			is = resourceLoader.getResource("classpath:usbreset.sh").getInputStream();
			
			File bin = new File("usbreset.sh");
			FileUtils.copyInputStreamToFile(is, bin);
			IOUtils.closeQuietly(is);
			
			Set<PosixFilePermission> perms = new HashSet<>();
			perms.add(PosixFilePermission.OWNER_EXECUTE);
			perms.add(PosixFilePermission.OWNER_READ);
			Files.setPosixFilePermissions(bin.toPath(), perms);
			
			this.sshPath = bin.getAbsolutePath();
		}
		catch(Exception e)
		{
			LOGGER.error("Unable to load usbreset.sh: " + e.getMessage());
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
	}
	
	public void runTask() throws ServiceException
	{
		List<STFDevice> devices = stfService.getAllDevices();
		for(STFDevice device : devices)
		{
			if(!(device.getPresent() && device.getReady()))
			{
				LOGGER.info(device.getModel() + " (" + device.getSerial() + ") - disconnected, trying to reset USB...");
				LOGGER.info("Running script: " + this.sshPath);
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
