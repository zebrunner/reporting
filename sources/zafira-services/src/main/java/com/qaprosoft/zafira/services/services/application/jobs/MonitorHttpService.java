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
package com.qaprosoft.zafira.services.services.application.jobs;

import com.qaprosoft.zafira.models.db.Monitor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MonitorHttpService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MonitorHttpService.class);

    public Integer getResponseCode(Monitor monitor)
    {
        int responseCode = 0;

        HttpClient httpClient = HttpClientBuilder.create().build();
        switch (monitor.getHttpMethod())
        {
            case GET:
            {
                try
                {
                    HttpGet request = new HttpGet(monitor.getUrl());
                    request.addHeader("Accept", "*/*");
                    responseCode = httpClient.execute(request).getStatusLine().getStatusCode();
                }
                catch (Exception e)
                {
                    LOGGER.error(e.getMessage());
                }
                break;
            }
            case PUT:
            {
                try
                {
                    HttpPut request = new HttpPut(monitor.getUrl());
                    request.addHeader("Content-Type", "application/json");
                    request.addHeader("Accept", "*/*");
                    request.setEntity(new StringEntity(monitor.getRequestBody(), "UTF-8"));
                    responseCode = httpClient.execute(request).getStatusLine().getStatusCode();
                }
                catch (Exception e)
                {
                    LOGGER.error(e.getMessage());
                }
                break;
            }
            case POST:
            {
                try
                {
                    HttpPost request = new HttpPost(monitor.getUrl());
                    request.addHeader("Content-Type", "application/json");
                    request.addHeader("Accept", "*/*");
                    request.setEntity(new StringEntity(monitor.getRequestBody(), "UTF-8"));
                    responseCode = httpClient.execute(request).getStatusLine().getStatusCode();
                }
                catch (Exception e)
                {
                    LOGGER.error(e.getMessage());
                }
                break;
            }
            default:
                break;
        }
        return responseCode;
    }
}
