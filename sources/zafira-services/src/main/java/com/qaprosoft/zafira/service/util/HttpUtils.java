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
package com.qaprosoft.zafira.service.util;

import kong.unirest.Config;
import kong.unirest.HttpResponse;
import kong.unirest.UnirestException;
import kong.unirest.UnirestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

public class HttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    private static final UnirestInstance restClient;

    static {
        Config config = new Config();
        config.connectTimeout(5000);
        config.cookieSpec("standard");
        restClient = new UnirestInstance(config);
    }

    /**
     * Checks that GET request to baseUrl returns 200 status
     * @param baseUrl - predefined url
     * @param username - username to build an url with basic auth
     * @param password - password to build an url with basic auth
     * @param path - path to check
     * @param replacePath - true if need to replace baseUrl endpoint with path
     * @return true if status is OK
     */
    public static boolean isReachable(String baseUrl, String username, String password, String path, boolean replacePath) {
        HttpResponse<?> response = null;
        try {
            String url = replacePath ? retrieveServletPath(baseUrl) : baseUrl;
            String pathToCheck = url + path;
            String basicAuthHeaderValue = getBasicAuthHeaderValue(username, password);
            response = restClient.get(pathToCheck)
                                 .header("Authorization", basicAuthHeaderValue)
                                 .asEmpty();
        } catch (MalformedURLException e) {
            LOGGER.error(String.format("Unable to verify connectivity for malformed url '%s'", baseUrl), e);
        } catch (UnirestException e) {
            LOGGER.error(String.format("Unable to check connectivity for url '%s'", baseUrl), e);
        }
        return response != null && response.getStatus() >= 200 && response.getStatus() <= 299;
    }

    public static boolean isReachable(String baseUrl, String username, String password, String path) {
        return isReachable(baseUrl, username, password, path, true);
    }

    private static String getBasicAuthHeaderValue(String username, String password) {
        String basicAuthHeaderValue = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        return "Basic " + basicAuthHeaderValue;
    }

    public static String buildBasicAuthUrl(String baseUrl, String username, String password) {
        String result = null;
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            String[] urlSlices = baseUrl.split("//");
            if (urlSlices.length == 2) {
                result = String.format("%s//%s:%s@%s", urlSlices[0], username, password, urlSlices[1]);
            }
        }
        return result != null ? result : baseUrl;
    }

    private static String retrieveServletPath(String originalUrl) throws MalformedURLException {
        URL url = new URL(originalUrl);
        return originalUrl.split(url.getPath())[0];
    }

    public static String retrievePath(String originalUrl) {
        String result = null;
        try {
            URL url = new URL(originalUrl);
            result = url.getPath();
        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }
}
