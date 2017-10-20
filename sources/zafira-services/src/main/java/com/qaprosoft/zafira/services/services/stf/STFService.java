package com.qaprosoft.zafira.services.services.stf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.services.services.jmx.CryptoService;
import com.qaprosoft.zafira.services.services.jmx.IJMXService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.client.STFClient;
import com.qaprosoft.zafira.models.stf.RemoteConnectUserDevice;
import com.qaprosoft.zafira.models.stf.STFDevice;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

import javax.annotation.PostConstruct;

import static com.qaprosoft.zafira.models.db.Setting.Tool.STF;

@ManagedResource(objectName = "bean:name=stfService", description = "STF init Managed Bean",
		currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200)
public class STFService implements IJMXService
{
	private static final Logger LOGGER = Logger.getLogger(STFService.class);

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private CryptoService cryptoService;

	private STFClient stfClient;

	@Override
	@PostConstruct
	public void init()
	{
		String url = null;
		String token = null;

		try
		{
			List<Setting> stfSettings = settingsService.getSettingsByTool(STF);
			for (Setting setting : stfSettings)
			{
				if (setting.isEncrypted())
				{
					setting.setValue(cryptoService.decrypt(setting.getValue()));
				}
				switch (Setting.SettingType.valueOf(setting.getName()))
				{
				case STF_URL:
					url = setting.getValue();
					break;
				case STF_TOKEN:
					token = setting.getValue();
					break;
				default:
					break;
				}
			}
			init(url, token);
		} catch (Exception e)
		{
			LOGGER.error("Setting does not exist", e);
		}
	}

	@ManagedOperation(description = "Change STF initialization")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "url", description = "STF url"),
			@ManagedOperationParameter(name = "token", description = "STF token")})
	public void init(String url, String token)
	{
		try
		{
			if (!StringUtils.isEmpty(url) && !StringUtils.isEmpty(token))
			{
				this.stfClient = new STFClient(url, token);
			}
		} catch (Exception e)
		{
			LOGGER.error("Unable to initialize Jenkins integration: " + e.getMessage());
		}
	}

	public List<STFDevice> getAllDevices()
	{
		return stfClient.getAllDevices().getObject().getDevices();
	}

	public Map<String, STFDevice> getAllDevicesAsMap() throws ServiceException
	{
		Map<String, STFDevice> devices = new HashMap<>();
		for (STFDevice device : getAllDevices())
		{
			devices.put(device.getSerial(), device);
		}
		return devices;
	}

	public RemoteConnectUserDevice connectDevice(String serial, long connectTimeoutSec)
	{
		RemoteConnectUserDevice device = null;
		if (stfClient.reserveDevice(serial, TimeUnit.SECONDS.toMillis(connectTimeoutSec)))
		{
			device = stfClient.remoteConnectDevice(serial).getObject();
		}
		return device;
	}

	public boolean disconnectDevice(String serial)
	{
		return stfClient.remoteDisconnectDevice(serial) && stfClient.returnDevice(serial);
	}

	@Override public boolean isConnected()
	{
		boolean isConnected;
		try
		{
			isConnected = getStfClient() != null && getStfClient().isConnected();
		} catch(Exception e)
		{
			isConnected = false;
		}
		return isConnected;
	}

	@ManagedAttribute(description = "Get STF client")
	public STFClient getStfClient()
	{
		return stfClient;
	}
}