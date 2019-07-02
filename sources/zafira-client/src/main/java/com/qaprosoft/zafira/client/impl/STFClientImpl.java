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
package com.qaprosoft.zafira.client.impl;

import com.qaprosoft.zafira.client.Path;
import com.qaprosoft.zafira.util.http.HttpClient;

import com.qaprosoft.zafira.models.stf.Devices;
import com.qaprosoft.zafira.models.stf.RemoteConnectUserDevice;
import com.qaprosoft.zafira.models.stf.STFDevice;

import java.util.HashMap;
import java.util.Map;

public class STFClientImpl implements com.qaprosoft.zafira.client.STFClient {

    private String serviceURL;
    private String authToken;

    public STFClientImpl(String serviceURL, String authToken) {
        this.serviceURL = serviceURL;
        this.authToken = authToken;
    }

    @Override
    public HttpClient.Response<Devices> getAllDevices() {
        return HttpClient.uri(Path.DEVICES_PATH, serviceURL)
                  .withAuthorization(buildAuthToken(authToken))
                  .get(Devices.class);
    }

    @Override
    public HttpClient.Response<STFDevice> getDevice(String udid) {
        return HttpClient.uri(Path.DEVICES_ITEM_PATH, serviceURL, udid)
                         .withAuthorization(buildAuthToken(authToken))
                         .get(STFDevice.class);
    }

    @Override
    public boolean reserveDevice(String serial, long timeout) {
        Map<String, String> entity = new HashMap<>();
        entity.put("serial", serial);
        HttpClient.Response response = HttpClient.uri(Path.USER_DEVICES_PATH, serviceURL)
                         .withAuthorization(buildAuthToken(authToken))
                         .post(Void.class, entity);
        return response.getStatus() == 200;
    }

    @Override
    public boolean returnDevice(String serial) {
        HttpClient.Response response = HttpClient.uri(Path.USER_DEVICES_BY_ID_PATH, serviceURL, serial)
                                                 .withAuthorization(buildAuthToken(authToken))
                                                 .delete(Void.class);
        return response.getStatus() == 200;
    }

    @Override
    public HttpClient.Response<RemoteConnectUserDevice> remoteConnectDevice(String serial) {
        return HttpClient.uri(Path.USER_DEVICES_REMOTE_CONNECT_PATH, serviceURL, serial)
                         .withAuthorization(buildAuthToken(authToken))
                         .post(RemoteConnectUserDevice.class, null);
    }

    @Override
    public boolean remoteDisconnectDevice(String serial) {
        HttpClient.Response response = HttpClient.uri(Path.USER_DEVICES_REMOTE_CONNECT_PATH, serviceURL, serial)
                                                 .withAuthorization(buildAuthToken(authToken))
                                                 .post(Void.class, null);
        return response.getStatus() == 200;
    }

    @Override
    public boolean isConnected() {
        HttpClient.Response response = HttpClient.uri(Path.DEVICES_PATH, serviceURL)
                                                 .withAuthorization(buildAuthToken(authToken))
                                                 .get(Devices.class);
        return response.getStatus() == 200;
    }

    private String buildAuthToken(String authToken) {
        return "Bearer " + authToken;
    }

}
