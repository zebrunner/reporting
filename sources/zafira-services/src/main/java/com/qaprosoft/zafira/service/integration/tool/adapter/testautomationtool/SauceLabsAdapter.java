package com.qaprosoft.zafira.service.integration.tool.adapter.testautomationtool;

import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.service.integration.tool.adapter.AdapterParam;
import com.qaprosoft.zafira.service.util.UrlUtils;
import kong.unirest.UnirestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.MalformedURLException;

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
        try {
            String usersPath = String.format(HEALTH_CHECK_PATH_PATTERN, username);
            return UrlUtils.verifyStatusByPath(usersPath, username, accessKey, "", false) &&
                    UrlUtils.verifyStatusByPath(url, username, accessKey, "/status", false);
        } catch (UnirestException | MalformedURLException e) {
            LOGGER.error("Unable to check SauceLabs connectivity", e);
            return false;
        }
    }

    @Override
    public String buildUrl() {
        return UrlUtils.buildBasicAuthUrl(url, username, accessKey);
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
