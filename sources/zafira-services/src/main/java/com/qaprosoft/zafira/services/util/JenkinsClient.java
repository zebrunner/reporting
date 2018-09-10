/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
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
package com.qaprosoft.zafira.services.util;

import java.net.URI;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.offbytwo.jenkins.client.JenkinsHttpClient;

/**
 * JenkinsClient - wraps {@link JenkinsHttpClient} for HTTP timeout configuration.
 * 
 * @author akhursevich
 */
public class JenkinsClient extends JenkinsHttpClient {

    public JenkinsClient(URI uri, JenkinsConfig config) {
        super(uri, configureHttpClient(uri, config));
        HttpContext ctxt = new BasicHttpContext();
        ctxt.setAttribute("preemptive-auth", new BasicScheme());
        setLocalContext(ctxt);
    }

    /**
     * Allows to configure HTTP connection timeouts for save Jenkins integration.
     * 
     * @param uri - Jenkins URI
     * @param username
     * @param password
     * @return configured {@link HttpClientBuilder}
     */
    private static HttpClientBuilder configureHttpClient(URI uri, JenkinsConfig config) {
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setConnectTimeout(config.getTimeout());
        requestBuilder = requestBuilder.setConnectionRequestTimeout(config.getTimeout());

        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(requestBuilder.build());

        return addAuthentication(builder, uri, config.getUsername(), config.getPassword());
    }
}
