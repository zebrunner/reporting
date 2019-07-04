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
import com.qaprosoft.zafira.util.http.HttpClient;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.tree.MergeCombiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.qaprosoft.zafira.client.ClientDefaults.ZAFIRA_PROPERTIES_FILE;

/**
 * ZafiraSingleton - singleton wrapper around {@link ZafiraClientImpl}.
 * 
 * @author Alexey Khursevich (hursevich@gmail.com)
 */
public enum ZafiraSingleton {

    INSTANCE;

    private final Logger LOGGER = Logger.getLogger(ZafiraSingleton.class);

    private ZafiraClient zafiraClient;

    private final CompletableFuture<HttpClient.Response<AuthTokenType>> INIT_FUTURE;

    ZafiraSingleton() {
        INIT_FUTURE = CompletableFuture.supplyAsync(() -> {
            HttpClient.Response<AuthTokenType> result = null;
            try {
                CombinedConfiguration config = new CombinedConfiguration(new MergeCombiner());
                // TODO: 2019-04-12 it`s make sense to throw an exception until zafira client instance is static in log appender class
                // and CombinedConfiguration doesn`t save singleton initialization 'injection'
                // config.setThrowExceptionOnMissing(false);
                config.addConfiguration(new SystemConfiguration());
                config.addConfiguration(getConfiguration());

                boolean enabled = config.getBoolean("zafira_enabled", false);
                String url = config.getString("zafira_service_url", StringUtils.EMPTY);
                String token = config.getString("zafira_access_token", StringUtils.EMPTY);

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

    private FileBasedConfiguration getConfiguration() throws org.apache.commons.configuration2.ex.ConfigurationException {
        return new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(new Parameters().properties().setFileName(ZAFIRA_PROPERTIES_FILE))
                .getConfiguration();
    }

    /**
     * @return {@link ZafiraClientImpl} instance
     */
    public ZafiraClient getClient() {
        return isRunning() ? zafiraClient : null;
    }

    /**
     * 
     * @return Zafira integration status
     */
    public boolean isRunning() {
        HttpClient.Response<AuthTokenType> response;
        try {
            response = INIT_FUTURE.get(60, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return false;
        } catch (Exception e) {
            LOGGER.debug("Cannot connect to zafira", e);
            return false;
        }
        return response != null && response.getStatus() == 200;
    }

}
