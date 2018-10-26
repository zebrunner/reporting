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
package com.qaprosoft.zafira.services.services.application.jmx.context;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class CloudFrontContext extends AbstractContext
{

    private static final Logger LOGGER = Logger.getLogger(CloudFrontContext.class);

    private String distributionDomain;
    private String keyPairId;
    private File privateKey;

    public CloudFrontContext(String distributionDomain, String keyPairId, byte[] privateKey, String filename)
    {
        this.distributionDomain = distributionDomain;
        this.keyPairId = keyPairId;
        this.privateKey = new File("./" + filename);
        try {
            FileUtils.writeByteArrayToFile(this.privateKey, privateKey);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public String getDistributionDomain() {
        return distributionDomain;
    }

    public void setDistributionDomain(String distributionDomain) {
        this.distributionDomain = distributionDomain;
    }

    public String getKeyPairId() {
        return keyPairId;
    }

    public void setKeyPairId(String keyPairId) {
        this.keyPairId = keyPairId;
    }

    public File getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(File privateKey) {
        this.privateKey = privateKey;
    }
}
