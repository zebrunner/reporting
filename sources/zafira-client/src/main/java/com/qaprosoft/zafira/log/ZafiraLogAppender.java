/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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

import com.qaprosoft.zafira.client.ZafiraClient.Response;
import com.qaprosoft.zafira.client.ZafiraSingleton;
import com.qaprosoft.zafira.listener.ZafiraListener;
import com.qaprosoft.zafira.models.dto.TestType;
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
	private int port = 5672;
	private String username = "guest";
	private String password = "guest";
	private String virtualHost = "/";
	private String exchange = "logs";
	private String type = "x-recent-history";
	private boolean durable = false;
	private String routingKey = "";
	private boolean zafiraConnected = false;
	private int history = 1000;
	
	private ExecutorService threadPool = Executors.newCachedThreadPool();

	/**
	 * Submits LoggingEvent for publishing if it reaches severity threshold.
	 * 
	 * @param loggingEvent - log event
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
		
		zafiraConnected = this.connectZafira();
		
		if(zafiraConnected)
		{
			// Creating connection
			try
			{
				this.createConnection();
			}
			catch (Exception e)
			{
				errorHandler.error("Unable to create RabbitMQ connection: " + e.getMessage());
			}

			// Creating channel
			try
			{
				this.createChannel();
			}
			catch (Exception e)
			{
				errorHandler.error("Unable to create RabbitMQ channel: " + e.getMessage());
			}

			// Create exchange
			try
			{
				this.createExchange();
			}
			catch (Exception e)
			{
				errorHandler.error("Unable to create RabbitMQ exchange: " + e.getMessage());
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
	 * @return identifier
	 */
	public String getIdentifier()
	{
		return identifier;
	}

	/**
	 * Sets identifier property from parameter in appender configuration
	 * 
	 * @param identifier - identifier property
	 */
	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	/**
	 * Returns host property as set in appender configuration
	 * 
	 * @return host
	 */
	public String getHost()
	{
		return host;
	}

	/**
	 * Sets host property from parameter in appender configuration
	 * 
	 * @param host - host property
	 */
	public void setHost(String host)
	{
		this.host = host;
	}

	/**
	 * Returns port property as set in appender configuration
	 * 
	 * @return port
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * Sets port property from parameter in appender configuration
	 * 
	 * @param port - port property
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	/**
	 * Returns username property as set in appender configuration
	 * 
	 * @return username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Sets username property from parameter in appender configuration
	 * 
	 * @param username - username property
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * Returns password property as set in appender configuration
	 * 
	 * @return password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets password property from parameter in appender configuration
	 * 
	 * @param password - password property
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Returns virtualHost property as set in appender configuration
	 * 
	 * @return virtualHost
	 */
	public String getVirtualHost()
	{
		return virtualHost;
	}

	/**
	 * Sets virtualHost property from parameter in appender configuration
	 * 
	 * @param virtualHost - virtualHost property
	 */
	public void setVirtualHost(String virtualHost)
	{
		this.virtualHost = virtualHost;
	}

	/**
	 * Returns exchange property as set in appender configuration
	 * 
	 * @return exchange
	 */
	public String getExchange()
	{
		return exchange;
	}

	/**
	 * Sets exchange property from parameter in appender configuration
	 * 
	 * @param exchange - exchange property
	 */
	public void setExchange(String exchange)
	{
		this.exchange = exchange;
	}

	/**
	 * Returns type property as set in appender configuration
	 * 
	 * @return type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Sets type property from parameter in appender configuration
	 * 
	 * @param type - type property
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	public boolean isDurable()
	{
		return durable;
	}

	/**
	 * Sets durable property from parameter in appender configuration
	 * 
	 *  @param durable - durable property
	 */
	public void setDurable(boolean durable)
	{
		this.durable = durable;
	}

	/**
	 * Returns routingKey property as set in appender configuration
	 * 
	 * @return routingKey
	 */
	public String getRoutingKey()
	{
		return routingKey;
	}

	/**
	 * Sets routingKey property from parameter in appender configuration
	 * 
	 * @param routingKey - routingKey property
	 */
	public void setRoutingKey(String routingKey)
	{
		this.routingKey = routingKey;
	}
	
	/**
	 * Returns history property as set in appender configuration
	 * 
	 * @return history
	 */
	public int getHistory() 
	{
		return history;
	}

	/**
	 * Sets history property from parameter in appender configuration
	 * 
	 * @param history - history property
	 */
	public void setHistory(int history) 
	{
		this.history = history;
	}

	/**
	 * Declares the exchange on RabbitMQ server according to properties set
	 */
	private void createExchange() throws IOException
	{
		if (this.channel != null && this.channel.isOpen())
		{
			synchronized (this.channel)
			{
				Map<String, Object> args = new HashMap<String, Object>();
				args.put(this.type, history);
				channel.exchangeDeclare(this.exchange, "x-recent-history", false, false, args);
			}
		}
	}

	/**
	 * Creates channel on RabbitMQ server
	 * 
	 * @return channel
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
	 * @return connection
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
	
	/**
	 * Connects to Zafira API and retrieves RabbitMQ configuration.
	 * 
	 * @return connection status
	 */
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
			
			if(ZafiraSingleton.INSTANCE.isRunning()) {
				// Queue referenced to ci_run_id
				this.routingKey = config.getString("ci_run_id", null);
				if(StringUtils.isEmpty(routingKey))
				{
					this.routingKey = UUID.randomUUID().toString();
					System.setProperty("ci_run_id", routingKey);
				}
				
				Response<List<HashMap<String, String>>> rs = ZafiraSingleton.INSTANCE.getClient().getToolSettings("RABBITMQ", true);
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
	 * @return requires layout flag
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
		String correlationId;
		
		LoggingEvent loggingEvent;

		AppenderTask(LoggingEvent loggingEvent)
		{
			this.loggingEvent = loggingEvent;
			Long testId = null;
			if(loggingEvent.getLevel().equals(MetaInfoLevel.META_INFO) && ((MetaInfoMessage) loggingEvent.getMessage()).getHeaders().get("TEST_ID") != null) {
				testId = Long.valueOf(((MetaInfoMessage) loggingEvent.getMessage()).getHeaders().get("TEST_ID"));
			} else {
				TestType test = ZafiraListener.getTestbythread().get(Thread.currentThread().getId());
				testId = test != null ? test.getId() : null;
			}
			this.correlationId = testId != null ? routingKey + "_" + String.valueOf(testId) : routingKey;
		}

		/**
		 * Method is called by ExecutorService and publishes message on RabbitMQ
		 * 
		 * @return loggingEvent
		 */
		@Override
		public LoggingEvent call() throws Exception
		{
			if(zafiraConnected)
			{
				String payload = layout.format(loggingEvent);
				
				AMQP.BasicProperties.Builder b = new AMQP.BasicProperties().builder();
				b.appId(identifier)
						.type(loggingEvent.getLevel().toString())
						.correlationId(String.valueOf(correlationId))
						.contentType("text/json");

				createChannel().basicPublish(exchange, routingKey, b.build(), payload.toString().getBytes());
			}

			return loggingEvent;
		}
	}
}
