/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.integration.impl;

import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import com.qaprosoft.zafira.services.services.application.integration.context.SeleniumContext;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;

import static com.qaprosoft.zafira.models.db.Setting.Tool.SELENIUM;

@Component
public class SeleniumService extends AbstractIntegration<SeleniumContext> {

    private static final CloseableHttpClient HTTP_CLIENT;
    private static final RequestConfig REQUEST_CONFIG;

    static {
        REQUEST_CONFIG = RequestConfig.custom()
                                      .setConnectTimeout(10)
                                      .setConnectionRequestTimeout(10)
                                      .build();
        HTTP_CLIENT = HttpClientBuilder.create().build();
    }

    public SeleniumService(SettingsService settingsService, CryptoService cryptoService) {
        super(settingsService, cryptoService, SELENIUM, SeleniumContext.class);
    }

    @Override
    public boolean isConnected() {
        boolean result;
        HttpGet request = null;
        CloseableHttpResponse response = null;
        try {
            String url = context().getUrl();
            request = new HttpGet(url);
            request.setConfig(REQUEST_CONFIG);
            response = HTTP_CLIENT.execute(request);

            result = response.getStatusLine().getStatusCode() == 200;
        } catch (Exception e) {
            result = false;
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
            if(response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return result;
    }

    @PreDestroy
    public void close() {
        try {
            if (HTTP_CLIENT != null) {
                HTTP_CLIENT.close();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
