/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.services.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URISyntaxException;

@Component
public class GitHubHttpUtils {

    private static final Logger LOGGER = Logger.getLogger(GitHubHttpUtils.class);

    private static final String GIT_HUB_ACCESS_TOKEN_PATH = "https://github.com/login/oauth/access_token";

    private CloseableHttpClient httpClient;

    public GitHubHttpUtils() {
        this.httpClient = HttpClientBuilder.create().build();
    }

    public String getAccessToken(String code, String clientId, String secret) throws URISyntaxException, IOException {
        HttpResponse httpResponse = this.httpClient.execute(buildGetAccessTokenRequest(code, clientId, secret));
        return getAccessToken(EntityUtils.toString(httpResponse.getEntity()));
    }

    private static HttpPost buildGetAccessTokenRequest(String code, String clientId, String secret) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(GIT_HUB_ACCESS_TOKEN_PATH);
        uriBuilder.addParameter("client_id", clientId)
                .addParameter("client_secret", secret)
                .addParameter("code", code)
                //.addParameter("redirect_uri", "on-access-token")
                .addParameter("accept", "json");
        return new HttpPost(uriBuilder.build());
    }

    private String getAccessToken(String response) {
        return response.split("access_token=")[1].split("&")[0];
    }

    @PreDestroy
    public void close() {
        try {
            if(httpClient != null)
            this.httpClient.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
