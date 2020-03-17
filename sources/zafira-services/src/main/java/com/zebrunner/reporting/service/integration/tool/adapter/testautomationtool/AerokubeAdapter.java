package com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool;

import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;
import com.zebrunner.reporting.service.util.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class AerokubeAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private final String url;
    private final String username;
    private final String accessKey;

    public AerokubeAdapter(Integration integration) {
        super(integration);
        this.url = getAttributeValue(integration, Parameter.URL);
        this.username = getAttributeValue(integration, Parameter.USERNAME);
        this.accessKey = getAttributeValue(integration, Parameter.PASSWORD);
    }

    @Override
    public boolean isConnected() {
        return HttpUtils.isReachable(url, username, accessKey, "/quota") &&
                HttpUtils.isReachable(url, username, accessKey, "/status", false);
    }

    @Override
    public String buildUrl() {
        return HttpUtils.buildBasicAuthUrl(url, username, accessKey);
    }

    @Getter
    @AllArgsConstructor
    private enum Parameter implements AdapterParam {
        URL("AEROKUBE_URL"),
        USERNAME("AEROKUBE_USER"),
        PASSWORD("AEROKUBE_PASSWORD");

        private final String name;
    }
}
