package com.qaprosoft.zafira.services.services.thirdparty.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qaprosoft.zafira.dbaccess.model.push.AbstractPush;
import com.qaprosoft.zafira.services.util.xmpp.XMPPConnectionManager;

@Service
public class XMPPService implements IPushService
{
	private static final Logger logger = LoggerFactory.getLogger(XMPPService.class);
	
	@Autowired
	@Qualifier(value="xmppConnectionManager")
	private XMPPConnectionManager cm;
	
	@Autowired
	@Qualifier("messageJsonifier")
	private ObjectMapper messageJsonifier;
	
	@Value("${zafira.jabber.enabled}")
	private boolean enabled;
	
	@Value("${zafira.jabber.username}")
	private String username;
	
	@Value("${zafira.jabber.password}")
	private String password;
	
	@Value("${zafira.jabber.http.bind}")
	private String httpBind;

	public void publish(String channel, AbstractPush push)
	{
		if(enabled)
		{
			try
			{
				cm.getChatManager().createChat(channel, cm.getMessageListener()).sendMessage(messageJsonifier.writeValueAsString(push));
			} catch (Exception e)
			{
				logger.error(e.getMessage());
			}
		}
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	public String getHttpBind()
	{
		return httpBind;
	}
}