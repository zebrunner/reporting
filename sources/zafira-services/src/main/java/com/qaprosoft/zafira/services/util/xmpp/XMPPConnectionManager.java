package com.qaprosoft.zafira.services.util.xmpp;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPConnectionManager
{
	private ChatManager chatManager;
	
	private MessageListener messageListener;
	
	public XMPPConnectionManager(ConnectionConfiguration config, String username, String password) throws XMPPException
	{
		XMPPConnection xmppConnection = new XMPPConnection(config);
		xmppConnection.connect();
		xmppConnection.login(username, password);
		this.chatManager = xmppConnection.getChatManager();
		this.messageListener = new XMPPMessageListener();
	}

	public ChatManager getChatManager()
	{
		return chatManager;
	}

	public void setChatManager(ChatManager chatManager)
	{
		this.chatManager = chatManager;
	}

	public MessageListener getMessageListener()
	{
		return messageListener;
	}

	public void setMessageListener(MessageListener messageListener)
	{
		this.messageListener = messageListener;
	}
}