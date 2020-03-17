package com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool;

import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;
import com.zebrunner.reporting.service.util.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class SauceLabsAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private static final String HEALTH_CHECK_PATH_PATTERN = "https://saucelabs.com/rest/v1/users/%s";

    private final String url;
    private final String username;
    private final String accessKey;

    public SauceLabsAdapter(Integration integration) {
        super(integration);
        this.url = getAttributeValue(integration, Parameter.URL);
        this.username = getAttributeValue(integration, Parameter.USERNAME);
        this.accessKey = getAttributeValue(integration, Parameter.PASSWORD);
    }

    @Override
    public boolean isConnected() {
        String usersPath = String.format(HEALTH_CHECK_PATH_PATTERN, username);
        return HttpUtils.isReachable(usersPath, username, accessKey, "", false) &&
                HttpUtils.isReachable(url, username, accessKey, "/status", false);
    }

    @Override
    public String buildUrl() {
        return HttpUtils.buildBasicAuthUrl(url, username, accessKey);
    }

    @Getter
    @AllArgsConstructor
    private enum Parameter implements AdapterParam {
        URL("SAUCELABS_URL"),
        USERNAME("SAUCELABS_USER"),
        PASSWORD("SAUCELABS_PASSWORD");

        private final String name;
    }
}
