package com.qaprosoft.zafira.services.services.jmx.models;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;

public class RabbitMQType extends AbstractType
{

    private CachingConnectionFactory cachingConnectionFactory;
    private Connection connection;

    public RabbitMQType(String host, String port, String username, String password)
    {
        this.cachingConnectionFactory = new CachingConnectionFactory(host, Integer.parseInt(port));
        this.cachingConnectionFactory.setUsername(username);
        this.cachingConnectionFactory.setPassword(password);
        this.connection = this.cachingConnectionFactory.createConnection();
    }

    public CachingConnectionFactory getCachingConnectionFactory()
    {
        return cachingConnectionFactory;
    }

    public void setCachingConnectionFactory(CachingConnectionFactory cachingConnectionFactory)
    {
        this.cachingConnectionFactory = cachingConnectionFactory;
    }

    public Connection getConnection()
    {
        return connection;
    }

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }
}
