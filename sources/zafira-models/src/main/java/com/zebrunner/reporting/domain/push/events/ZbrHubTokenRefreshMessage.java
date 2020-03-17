package com.zebrunner.reporting.domain.push.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZbrHubTokenRefreshMessage extends EventMessage {

    public ZbrHubTokenRefreshMessage(String tenantName, String token) {
        super(tenantName);
        this.token = token;
    }

    private String token;
}
