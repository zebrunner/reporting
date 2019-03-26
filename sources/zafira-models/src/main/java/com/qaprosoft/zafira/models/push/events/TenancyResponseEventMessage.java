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
package com.qaprosoft.zafira.models.push.events;

public class TenancyResponseEventMessage extends EventMessage {

    private String token;
    private String source;
    private String zafiraLogoURL;
    private String zafiraURL;
    private Boolean success;

    public TenancyResponseEventMessage(String tenancy) {
        super(tenancy);
    }

    public TenancyResponseEventMessage(String tenancy, String token, String source, String zafiraLogoURL, String zafiraURL, Boolean success) {
        super(tenancy);
        this.token = token;
        this.source = source;
        this.zafiraLogoURL = zafiraLogoURL;
        this.zafiraURL = zafiraURL;
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getZafiraLogoURL() {
        return zafiraLogoURL;
    }

    public void setZafiraLogoURL(String zafiraLogoURL) {
        this.zafiraLogoURL = zafiraLogoURL;
    }

    public String getZafiraURL() {
        return zafiraURL;
    }

    public void setZafiraURL(String zafiraURL) {
        this.zafiraURL = zafiraURL;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
