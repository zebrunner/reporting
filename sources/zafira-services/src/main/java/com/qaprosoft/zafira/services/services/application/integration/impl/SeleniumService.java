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
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static com.qaprosoft.zafira.models.db.Setting.Tool.SELENIUM;

@Component
public class SeleniumService extends AbstractIntegration<SeleniumContext> {

    private static final int TIMEOUT = 5000;
    private static final HttpClient HTTP_CLIENT;

    static {
        HTTP_CLIENT = HttpClient.newBuilder()
                                .connectTimeout(Duration.ofMillis(TIMEOUT))
                                .build();
    }

    public SeleniumService(SettingsService settingsService, CryptoService cryptoService) {
        super(settingsService, cryptoService, SELENIUM, SeleniumContext.class);
    }

    @Override
    public boolean isConnected() {
        boolean result;
        HttpRequest request;
        HttpResponse response;
        try {
            String url = context().getUrl();
            request = HttpRequest.newBuilder()
                                 .uri(URI.create(url))
                                 .timeout(Duration.ofMillis(TIMEOUT))
                                 .GET()
                                 .build();
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            result = response.statusCode() == 200;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

}
