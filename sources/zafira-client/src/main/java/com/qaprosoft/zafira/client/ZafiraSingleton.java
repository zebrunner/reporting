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
 *******************************************************************************/
package com.qaprosoft.zafira.client;

import com.qaprosoft.zafira.client.impl.ZafiraClientImpl;
import com.qaprosoft.zafira.listener.domain.ZafiraConfiguration;
import com.qaprosoft.zafira.util.AsyncUtil;
import com.qaprosoft.zafira.util.ConfigurationUtil;
import com.qaprosoft.zafira.util.http.HttpClient;
import org.apache.commons.configuration2.CombinedConfiguration;

import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;

import java.util.concurrent.CompletableFuture;

/**
 * ZafiraSingleton - singleton wrapper around {@link ZafiraClientImpl}.
 * 
 * @author Alexey Khursevich (hursevich@gmail.com)
 */
public enum ZafiraSingleton {

    INSTANCE;

    private final CompletableFuture<HttpClient.Response<AuthTokenType>> INIT_FUTURE;

    private ZafiraClient zafiraClient;

    ZafiraSingleton() {
        INIT_FUTURE = CompletableFuture.supplyAsync(() -> {
            HttpClient.Response<AuthTokenType> result = null;
            try {
                CombinedConfiguration config = ConfigurationUtil.getConfiguration(false);
                // TODO: 2019-04-12 it`s make sense to throw an exception until zafira client instance is static in log appender class
                // and CombinedConfiguration doesn`t save singleton initialization 'injection'
                // config.setThrowExceptionOnMissing(false);

                boolean enabled = (Boolean) ZafiraConfiguration.ENABLED.get(config);
                String url = (String) ZafiraConfiguration.SERVICE_URL.get(config);
                String token = (String) ZafiraConfiguration.ACCESS_TOKEN.get(config);

                zafiraClient = new ZafiraClientImpl(url);
                if (enabled && zafiraClient.isAvailable()) {
                    result = zafiraClient.refreshToken(token);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return result;
        });

        INIT_FUTURE.thenAccept(auth -> {
            if (auth != null && auth.getStatus() == 200) {
                zafiraClient.setAuthToken(auth.getObject().getType() + " " + auth.getObject().getAccessToken());
                //zafiraClient.onInit();
            }
        });
    }

    /**
     * @return {@link ZafiraClientImpl} instance
     */
    public ZafiraClient getClient() {
        return isRunning() ? zafiraClient : null;
    }

    /**
     * @return Zafira integration status
     */
    public boolean isRunning() {
        HttpClient.Response<AuthTokenType> response;
        try {
            response = AsyncUtil.getAsync(INIT_FUTURE, "Cannot connect to zafira");
        } catch (Exception e) {
            return false;
        }
        return response != null && response.getStatus() == 200;
    }

}
