package com.qaprosoft.zafira.services.services.jmx;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.SettingsService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.qaprosoft.zafira.models.db.Setting.Tool.RABBITMQ;

@ManagedResource(objectName="bean:name=rabbitMQService", description="RabbitMQ init Managed Bean",
		currencyTimeLimit=15, persistPolicy="OnUpdate", persistPeriod=200,
		persistLocation="foo", persistName="bar")
public class RabbitMQService implements IJMXService
{
	private static final Logger LOGGER = Logger.getLogger(RabbitMQService.class);

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private CryptoService cryptoService;

	private Boolean isConnected;

	@Override
	@PostConstruct
	public void init()
	{
		// TODO Auto-generated method stub
		String host = null;
		String port = null;
		String ws = null;
		String username = null;
		String password = null;
		boolean isConnected = false;

		try {
			List<Setting> rabbitmqSettings = settingsService.getSettingsByTool(RABBITMQ);
			for (Setting setting : rabbitmqSettings)
			{
				if(setting.isEncrypted())
				{
					setting.setValue(cryptoService.decrypt(setting.getValue()));
				}
				switch (Setting.SettingType.valueOf(setting.getName()))
				{
				case RABBITMQ_HOST:
					host = setting.getValue();
					break;
				case RABBITMQ_PORT:
					port = setting.getValue();
					break;
				case RABBITMQ_WS:
					ws = setting.getValue();
					break;
				case RABBITMQ_USER:
					username = setting.getValue();
					break;
				case RABBITMQ_PASSWORD:
					password = setting.getValue();
					break;
				case RABBITMQ_ENABLED:
					isConnected = Boolean.parseBoolean(setting.getValue());
				default:
					break;
				}
			}
			init(host, port, ws, username, password, isConnected);
		} catch(Exception e) {
			LOGGER.error("Setting does not exist", e);
		}
	}

	@ManagedOperation(description="Change RabbitMQ initialization")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "host", description = "RabbitMQ host"),
			@ManagedOperationParameter(name = "port", description = "RabbitMQ port"),
			@ManagedOperationParameter(name = "ws", description = "RabbitMQ ws"),
			@ManagedOperationParameter(name = "username", description = "RabbitMQ username"),
			@ManagedOperationParameter(name = "password", description = "RabbitMQ password"),
			@ManagedOperationParameter(name = "isConnected", description = "RabbitMQ is connected")})
	public void init(String host, String port, String ws, String username, String password, boolean isConnected){
		try
		{
			if (!StringUtils.isEmpty(host) && !StringUtils.isEmpty(port) && !StringUtils.isEmpty(username) && !StringUtils.isEmpty(password))
			{
				this.isConnected = isConnected;
			}
		} catch (Exception e)
		{
			LOGGER.error("Unable to initialize Jira integration: " + e.getMessage());
		}
	}

	@Override
	public boolean isConnected()
	{
		// TODO: add integration check
		return this.isConnected;
	}
}