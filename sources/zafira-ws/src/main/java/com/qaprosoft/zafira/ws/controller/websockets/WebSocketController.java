package com.qaprosoft.zafira.ws.controller.websockets;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.qaprosoft.zafira.dbaccess.model.push.AbstractPush;

@Controller
public class WebSocketController
{
	@MessageMapping("/tests/send")
	@SendTo("/topic/tests")
	public AbstractPush sendMessage(AbstractPush message) throws Exception
	{
		return message;
	}
}
