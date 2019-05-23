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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import static com.qaprosoft.zafira.models.db.Setting.Tool.SELENIUM;

@Component
public class SeleniumService extends AbstractIntegration<SeleniumContext> {

    private static final HttpClient HTTP_CLIENT;

    static {
        HTTP_CLIENT = HttpClientBuilder.create().build();
    }

    public SeleniumService(SettingsService settingsService, CryptoService cryptoService) {
        super(settingsService, cryptoService, SELENIUM, SeleniumContext.class);
    }

    @Override
    public boolean isConnected() {
        boolean result;
        try {
            String url = context().getUrl();
            HttpResponse response = HTTP_CLIENT.execute(new HttpGet(url));
            result = response.getStatusLine().getStatusCode() == 200;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

}
