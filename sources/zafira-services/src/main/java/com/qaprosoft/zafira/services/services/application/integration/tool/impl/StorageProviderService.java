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
package com.qaprosoft.zafira.services.services.application.integration.tool.impl;

import java.util.*;

import com.qaprosoft.zafira.services.services.application.integration.tool.AbstractIntegration;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.storageprovider.StorageProviderAdapter;

import com.qaprosoft.zafira.models.dto.aws.FileUploadType;
import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import org.springframework.stereotype.Component;

@Component
public class StorageProviderService extends AbstractIntegration<StorageProviderAdapter> {

    public StorageProviderService() {
        super("AMAZON");
    }

    public String saveFile(final FileUploadType file) {
        StorageProviderAdapter storageProviderAdapter = getDefaultAdapter();
        return storageProviderAdapter.saveFile(file);
    }

    public void removeFile(final String linkToFile) {
        StorageProviderAdapter storageProviderAdapter = getDefaultAdapter();
        storageProviderAdapter.removeFile(linkToFile);
    }

    /**
     * Generates temporary credentials for external clients
     * 
     * @return {@link SessionCredentials} object
     */
    public Optional<SessionCredentials> getTemporarySessionCredentials(int expiresIn) {
        StorageProviderAdapter storageProviderAdapter = getDefaultAdapter();
        return storageProviderAdapter.getTemporarySessionCredentials(expiresIn);
    }

}
