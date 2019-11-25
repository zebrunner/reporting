package com.qaprosoft.zafira.service.integration.tool.adapter.testautomationtool;

import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.service.integration.tool.adapter.AdapterParam;
import kong.unirest.Config;
import kong.unirest.HttpResponse;
import kong.unirest.UnirestException;
import kong.unirest.UnirestInstance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

public class AerokubeAdapter extends AbstractIntegrationAdapter implements TestAutomationToolAdapter {

    private final String url;
    private final String username;
    private final String accessKey;

    private final UnirestInstance restClient = initClient();

    public AerokubeAdapter(Integration integration) {
        super(integration);
        this.url = getAttributeValue(integration, Parameter.URL);
        this.username = getAttributeValue(integration, Parameter.USERNAME);
        this.accessKey = getAttributeValue(integration, Parameter.PASSWORD);
    }

    private UnirestInstance initClient() {
        Config config = new Config();
        config.connectTimeout(5000);
        return new UnirestInstance(config);
    }
    @Override
    public boolean isConnected() {
        try {
            HttpResponse response = restClient.get(url).asEmpty();
            return response.getStatus() == 200;
        } catch (UnirestException e) {
            LOGGER.error("Unable to check Aerokube connectivity", e);
            return false;
        }
    }

    @Override
    public String buildUrl() {
        String result = null;
        if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(accessKey)) {
            String[] urlSlices = url.split("//");
            result = String.format("%s//%s:%s@%s", urlSlices[0], username, accessKey, urlSlices[1]);
        }
        return result != null ? result : url;
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
