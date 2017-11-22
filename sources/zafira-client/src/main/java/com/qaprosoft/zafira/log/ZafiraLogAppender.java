package com.qaprosoft.zafira.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.tree.MergeCombiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.client.ZafiraClient.Response;
import com.qaprosoft.zafira.listener.ZafiraListener;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author akhursevich
 */
public class ZafiraLogAppender extends AppenderSkeleton
{
	private static final String ZAFIRA_PROPERTIES = "zafira.properties";
	
	private ConnectionFactory factory = new ConnectionFactory();
	private Connection connection = null;
	private Channel channel = null;
	private String identifier = null;
	private String host = "localhost";
	private int port = 5762;
	private String username = "guest";
	private String password = "guest";
	private String virtualHost = "/";
	private String exchange = "logs";
	private String type = "direct";
	private boolean durable = false;
	private String queue = "common";
	private String routingKey = "";
	private boolean enabled = false;
	
	private ExecutorService threadPool = Executors.newCachedThreadPool();

	/**
	 * Submits LoggingEvent for publishing if it reaches severity threshold.
	 * 
	 * @param loggingEvent
	 */
	@Override
	protected void append(LoggingEvent loggingEvent)
	{
		if (isAsSevereAsThreshold(loggingEvent.getLevel()))
		{
			threadPool.submit(new AppenderTask(loggingEvent));
		}
	}

	/**
	 * Creates the connection, channel to RabbitMQ. Declares exchange and queue
	 * 
	 * @see AppenderSkeleton
	 */
	@Override
	public void activateOptions()
	{
		super.activateOptions();
		
		enabled = this.connectZafira();
		
		if(enabled)
		{
			// Creating connection
			try
			{
				this.createConnection();
			}
			catch (Exception e)
			{
				errorHandler.error(e.getMessage(), e, ErrorCode.GENERIC_FAILURE);
			}

			// Creating channel
			try
			{
				this.createChannel();
			}
			catch (Exception e)
			{
				errorHandler.error(e.getMessage(), e, ErrorCode.GENERIC_FAILURE);
			}

			// Create exchange
			try
			{
				this.createExchange();
			}
			catch (Exception e)
			{
				errorHandler.error(e.getMessage(), e, ErrorCode.GENERIC_FAILURE);
			}

			// Create queue
			try
			{
				this.createQueue();
			}
			catch (Exception e)
			{
				errorHandler.error(e.getMessage(), e, ErrorCode.GENERIC_FAILURE);
			}
		}
	}

	/**
	 * Sets the ConnectionFactory parameters
	 */
	private void setFactoryConfiguration()
	{
		factory.setHost(this.host);
		factory.setPort(this.port);
		factory.setVirtualHost(this.virtualHost);
		factory.setUsername(this.username);
		factory.setPassword(this.password);
	}

	/**
	 * Returns identifier property as set in appender configuration
	 * 
	 * @return
	 */
	public String getIdentifier()
	{
		return identifier;
	}

	/**
	 * Sets identifier property from parameter in appender configuration
	 * 
	 * @param identifier
	 */
	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	/**
	 * Returns host property as set in appender configuration
	 * 
	 * @return
	 */
	public String getHost()
	{
		return host;
	}

	/**
	 * Sets host property from parameter in appender configuration
	 * 
	 * @param host
	 */
	public void setHost(String host)
	{
		this.host = host;
	}

	/**
	 * Returns port property as set in appender configuration
	 * 
	 * @return
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * Sets port property from parameter in appender configuration
	 * 
	 * @param port
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	/**
	 * Returns username property as set in appender configuration
	 * 
	 * @return
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Sets username property from parameter in appender configuration
	 * 
	 * @param username
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * Returns password property as set in appender configuration
	 * 
	 * @return
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets password property from parameter in appender configuration
	 * 
	 * @param password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Returns virtualHost property as set in appender configuration
	 * 
	 * @return
	 */
	public String getVirtualHost()
	{
		return virtualHost;
	}

	/**
	 * Sets virtualHost property from parameter in appender configuration
	 * 
	 * @param virtualHost
	 */
	public void setVirtualHost(String virtualHost)
	{
		this.virtualHost = virtualHost;
	}

	/**
	 * Returns exchange property as set in appender configuration
	 * 
	 * @return
	 */
	public String getExchange()
	{
		return exchange;
	}

	/**
	 * Sets exchange property from parameter in appender configuration
	 * 
	 * @param exchange
	 */
	public void setExchange(String exchange)
	{
		this.exchange = exchange;
	}

	/**
	 * Returns type property as set in appender configuration
	 * 
	 * @return
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Sets type property from parameter in appender configuration
	 * 
	 * @param type
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Returns queue property as set in appender configuration
	 * 
	 * @return
	 */
	public String getQueue()
	{
		return queue;
	}

	/**
	 * Sets host property from parameter in appender configuration
	 * 
	 * @param queue
	 */
	public void setQueue(String queue)
	{
		this.queue = queue;
	}

	public boolean isDurable()
	{
		return durable;
	}

	/**
	 * Sets durable property from parameter in appender configuration
	 * 
	 * @param durable
	 */
	public void setDurable(boolean durable)
	{
		this.durable = durable;
	}

	/**
	 * Returns routingKey property as set in appender configuration
	 * 
	 * @return
	 */
	public String getRoutingKey()
	{
		return routingKey;
	}

	/**
	 * Sets routingKey property from parameter in appender configuration
	 * 
	 * @param routingKey
	 */
	public void setRoutingKey(String routingKey)
	{
		this.routingKey = routingKey;
	}

	/**
	 * Declares the exchange on RabbitMQ server according to properties set
	 * 
	 * @throws IOException
	 */
	private void createExchange() throws IOException
	{
		if (this.channel != null && this.channel.isOpen())
		{
			synchronized (this.channel)
			{
				this.channel.exchangeDeclare(this.exchange, this.type, this.durable);
			}
		}
	}

	/**
	 * Declares and binds queue on rabbitMQ server according to properties
	 * 
	 * @throws IOException
	 */
	private void createQueue() throws IOException
	{
		if (this.channel != null && this.channel.isOpen())
		{
			synchronized (this.channel)
			{
				this.channel.queueDeclare(this.queue, true, false, false, null);
				this.channel.queueBind(this.queue, this.exchange, this.routingKey);
			}
		}
	}

	/**
	 * Creates channel on RabbitMQ server
	 * 
	 * @return
	 * @throws IOException
	 */
	private Channel createChannel() throws IOException
	{
		if (this.channel == null || !this.channel.isOpen() && (this.connection != null && this.connection.isOpen()))
		{
			this.channel = this.connection.createChannel();
		}
		return this.channel;
	}

	/**
	 * Creates connection to RabbitMQ server according to properties
	 * 
	 * @return
	 * @throws IOException
	 * @throws TimeoutException
	 */
	private Connection createConnection() throws IOException, TimeoutException
	{
		setFactoryConfiguration();
		if (this.connection == null || !this.connection.isOpen())
		{
			this.connection = factory.newConnection();
		}

		return this.connection;
	}
	
	
	private boolean connectZafira()
	{
		boolean connected = false;
		
		try
		{
			CombinedConfiguration config = new CombinedConfiguration(new MergeCombiner());
			config.setThrowExceptionOnMissing(true);
			config.addConfiguration(new SystemConfiguration());
			config.addConfiguration(new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				    					  .configure(new Parameters().properties().setFileName(ZAFIRA_PROPERTIES)).getConfiguration());
			
			if(config.getBoolean("zafira_enabled", false))
			{
				ZafiraClient zc = new ZafiraClient(config.getString("zafira_service_url"));
				if(zc.isAvailable())
				{
					Response<AuthTokenType> auth = zc.refreshToken(config.getString("zafira_access_token", null));
					if(auth.getStatus() == 200)
					{
						zc.setAuthToken(auth.getObject().getType() + " " + auth.getObject().getAccessToken());
					}
					else
					{
						throw new Exception("Not authenticated in Zafira!");
					}
					
					// Queue referenced to ci_run_id
					queue = config.getString("ci_run_id", null);
					if(StringUtils.isEmpty(queue))
					{
						queue = UUID.randomUUID().toString();
						System.setProperty("ci_run_id", queue);
					}
					
					Response<List<HashMap<String, String>>> rs = zc.getToolSettings("RABBITMQ");
					if(rs.getStatus() == 200)
					{
						List<HashMap<String, String>> settings = rs.getObject();
						if(settings != null)
						{
							for(HashMap<String, String> s : settings)
							{
								if("RABBITMQ_HOST".equals(s.get("name")))
								{
									this.host = s.get("value");
								}
								else if("RABBITMQ_PORT".equals(s.get("name")))
								{
									this.port = Integer.valueOf(s.get("value"));
								}
								else if("RABBITMQ_USER".equals(s.get("name")))
								{
									this.username = s.get("value");
								}
								else if("RABBITMQ_PASSWORD".equals(s.get("name")))
								{
									this.password = s.get("value");
								}
								else if("RABBITMQ_ENABLED".equals(s.get("name")))
								{
									connected = Boolean.valueOf(s.get("value"));
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e) 
		{
			// TODO: add logging
		}
		
		return connected;
	}

	/**
	 * Closes the channel and connection to RabbitMQ when shutting down the appender
	 */
	@Override
	public void close()
	{
		if (channel != null && channel.isOpen())
		{
			try
			{
				channel.close();
			}
			catch (IOException | TimeoutException ioe)
			{
				errorHandler.error(ioe.getMessage(), ioe, ErrorCode.CLOSE_FAILURE);
			}
		}

		if (connection != null && connection.isOpen())
		{
			try
			{
				this.connection.close();
			}
			catch (IOException ioe)
			{
				errorHandler.error(ioe.getMessage(), ioe, ErrorCode.CLOSE_FAILURE);
			}
		}
	}

	/**
	 * Ensures that a Layout property is required
	 * 
	 * @return
	 */
	@Override
	public boolean requiresLayout()
	{
		return true;
	}

	/**
	 * Simple Callable class that publishes messages to RabbitMQ server
	 */
	class AppenderTask implements Callable<LoggingEvent>
	{
		Long testId;
		
		LoggingEvent loggingEvent;

		Map<String, Object> headers = new HashMap<String, Object>();
		
		AppenderTask(LoggingEvent loggingEvent)
		{
			this.loggingEvent = loggingEvent;
			TestType test = ZafiraListener.getTestbythread().get(Thread.currentThread().getId());
			if(test != null)
			{
				headers.put("testId", test.getId());
				testId = test.getId();
			}
		}

		/**
		 * Method is called by ExecutorService and publishes message on RabbitMQ
		 * 
		 * @return
		 * @throws Exception
		 */
		@Override
		public LoggingEvent call() throws Exception
		{
			if(enabled)
			{
				String payload = layout.format(loggingEvent);
				
				String correlationId = String.valueOf(System.currentTimeMillis());
				
				AMQP.BasicProperties.Builder b = new AMQP.BasicProperties().builder();
				b.appId(identifier)
						.type(loggingEvent.getLevel().toString())
						.correlationId(String.valueOf(testId))
						.headers(headers)
						.contentType("text/json");

				createChannel().basicPublish(exchange, routingKey, b.build(), payload.toString().getBytes());
			}

			return loggingEvent;
		}
	}
}