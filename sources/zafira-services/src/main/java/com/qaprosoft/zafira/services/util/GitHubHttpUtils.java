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
package com.qaprosoft.zafira.services.util;

import com.qaprosoft.zafira.services.exceptions.ExternalSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class GitHubHttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubHttpUtils.class);

    private static final String GITHUB_ACCESS_TOKEN_PATH = "https://github.com/login/oauth/access_token";

    private HttpClient httpClient;

    public GitHubHttpUtils() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String getAccessToken(String code, String clientId, String secret) throws IOException {
        HttpResponse httpResponse;
        try {
            httpResponse = this.httpClient.send(buildGetAccessTokenRequest(code, clientId, secret), HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new ExternalSystemException(e.getMessage(), e);
        }
        return getAccessToken(httpResponse.body().toString());
    }

    private static HttpRequest buildGetAccessTokenRequest(String code, String clientId, String secret) {
        URI uri = new DefaultUriBuilderFactory(GITHUB_ACCESS_TOKEN_PATH).builder()
                                                                        .queryParam("client_id", clientId)
                                                                        .queryParam("client_secret", secret)
                                                                        .queryParam("code", code)
                                                                        .queryParam("accept", "json")
                                                                        .build();
        return HttpRequest.newBuilder()
                          .uri(uri)
                          .POST(HttpRequest.BodyPublishers.noBody())
                          .build();
    }

    private String getAccessToken(String response) {
        return response.split("access_token=")[1].split("&")[0];
    }

}
