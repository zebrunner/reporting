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
import kong.unirest.UnirestInstance;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

public class UrlUtils {

    private static final UnirestInstance restClient;

    static {
        Config config = new Config();
        config.connectTimeout(5000);
        restClient = new UnirestInstance(config);
    }

    /**
     * Varify that GET request to baseUrl returns 200 status
     * @param baseUrl - predefined url
     * @param username - username to build an url with basic auth
     * @param password - password to build an url with basic auth
     * @param path - path to check
     * @param replacePath - true if need to replace baseUrl endpoint with path
     * @return true if status is OK
     */
    public static boolean verifyStatusByPath(String baseUrl, String username, String password, String path, boolean replacePath) throws MalformedURLException {
        String basicAuthUrl = buildBasicAuthUrl(baseUrl, username, password);
        String url = replacePath ? retrieveServletPath(basicAuthUrl) : basicAuthUrl;
        String pathToCheck = url + path;
        String basicAuthHeaderValue = getBasicAuthHeaderValue(username, password);
        HttpResponse<?> response = restClient.get(pathToCheck)
                                             .header("Authorization", basicAuthHeaderValue)
                                             .asEmpty();
        return response.getStatus() == 200;
    }

    private static String getBasicAuthHeaderValue(String username, String password) {
        String basicAuthHeaderValue = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        return "Basic " + basicAuthHeaderValue;
    }

    public static boolean verifyStatusByPath(String baseUrl, String username, String password, String path) throws MalformedURLException {
        return verifyStatusByPath(baseUrl, username, password, path, true);
    }

    public static String buildBasicAuthUrl(String baseUrl, String username, String password) {
        String result = null;
        if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            String[] urlSlices = baseUrl.split("//");
            result = String.format("%s//%s:%s@%s", urlSlices[0], username, password, urlSlices[1]);
        }
        return result != null ? result : baseUrl;
    }

    private static String retrieveServletPath(String originalUrl) throws MalformedURLException {
        URL url = new URL(originalUrl);
        return originalUrl.split(url.getPath())[0];
    }

    public static String retrievePath(String originalUrl) throws MalformedURLException {
        URL url = new URL(originalUrl);
        return url.getPath();
    }
}
