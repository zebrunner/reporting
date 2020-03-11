package com.qaprosoft.zafira.models.push.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZbrHubTokenUpdateMessage extends EventMessage {

    public ZbrHubTokenUpdateMessage(String tenantName, String token) {
        super(tenantName);
        this.token = token;
    }

    private String token;
}
