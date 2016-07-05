package com.qaprosoft.zafira.services.util.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class XMPPMessageListener implements MessageListener
{
//	private static final Logger logger = LoggerFactory.getLogger(XMPPMessageListener.class);
	
	@Override
	public void processMessage(Chat chat, Message message)
	{
		// Do nothing for now
		// logger.info(String.format("Jabber message was sent to %s: '%s'", message.getTo(), message.getBody()));
	}
}