package com.qaprosoft.zafira.services.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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



    public static int sendPostAndGetResponseStatus(String url, String requestBody) {
        HttpClient client = HttpClients.createDefault();

        HttpPost request = new HttpPost(url);
        StringEntity params = null;
        try {
            params = new StringEntity(requestBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("Unsupprtde encoding!");
        }
        request.addHeader("content-type", "application/json");
        request.addHeader("Accept", "application/json");
        request.setEntity(params);
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            LOGGER.info("Wrong request!");
        }

        return response.getStatusLine().getStatusCode();
    }


    public static int sendPutAndGetResponseStatus(String url, String requestBody) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = null;
        HttpPut request = new HttpPut(url);
        StringEntity params = null;
        try {
            params = new StringEntity(requestBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("Unsupported encoding!");
        }
        params.setContentType("application/json");
        request.addHeader("content-type", "application/json");
        request.addHeader("Accept", "*/*");
        request.addHeader("Accept-Encoding", "gzip,deflate,sdch");
        request.addHeader("Accept-Language", "en-US,en;q=0.8");
        request.setEntity(params);
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            LOGGER.info("Wrong request!");
        }
        return response.getStatusLine().getStatusCode();
    }

}
