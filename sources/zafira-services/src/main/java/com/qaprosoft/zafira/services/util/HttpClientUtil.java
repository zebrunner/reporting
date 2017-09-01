package com.qaprosoft.zafira.services.util;

import com.qaprosoft.zafira.models.db.Monitor.HttpMethod;
import org.apache.commons.httpclient.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static org.apache.http.protocol.HTTP.USER_AGENT;

/**
 * @author Kirill Bugrim
 *
 * @version 1.0
 */


public class HttpClientUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    public static int sendGetAndGetResponseStatus(String url)  {

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            LOGGER.info("Unable to send request");
        }
        return response.getStatusLine().getStatusCode();

    }

}
