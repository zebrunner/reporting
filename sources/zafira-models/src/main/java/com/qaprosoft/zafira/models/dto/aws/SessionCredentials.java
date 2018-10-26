/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package com.qaprosoft.zafira.models.dto.aws;

import java.io.Serializable;

/**
 * An entity of minimal requirements for Amazon S3 integration
 */
public class SessionCredentials implements Serializable
{

    private static final long serialVersionUID = -2399949213318100097L;

    private String accessKeyId;
    private String secretAccessKey;
    private String sessionToken;
    private String region;
    private String bucket;

    public SessionCredentials()
    {
    }

    public SessionCredentials(String accessKeyId, String secretAccessKey, String sessionToken, String region, String bucket)
    {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.sessionToken = sessionToken;
        this.region = region;
        this.bucket = bucket;
    }

    public String getAccessKeyId()
    {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId)
    {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretAccessKey()
    {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey)
    {
        this.secretAccessKey = secretAccessKey;
    }

    public String getSessionToken()
    {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken)
    {
        this.sessionToken = sessionToken;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
