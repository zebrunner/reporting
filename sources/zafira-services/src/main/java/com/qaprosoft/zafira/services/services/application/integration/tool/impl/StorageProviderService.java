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

import com.qaprosoft.zafira.models.dto.aws.FileUploadType;
import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.AbstractIntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.storageprovider.StorageProviderAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.proxy.StorageProviderProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StorageProviderService extends AbstractIntegrationService<StorageProviderAdapter> {

    private final int storageProviderTokenExpiration;

    public StorageProviderService(IntegrationService integrationService,
                                  StorageProviderProxy storageProviderProxy,
                                  @Value("${amazon-token-expiration}") int storageProviderTokenExpiration
    ) {
        super(integrationService, storageProviderProxy, "AMAZON");
        this.storageProviderTokenExpiration = storageProviderTokenExpiration;
    }

    public String saveFile(final FileUploadType file) {
        StorageProviderAdapter storageProviderAdapter = getAdapterForIntegration(null);
        return storageProviderAdapter.saveFile(file);
    }

    public void removeFile(final String linkToFile) {
        StorageProviderAdapter storageProviderAdapter = getAdapterForIntegration(null);
        storageProviderAdapter.removeFile(linkToFile);
    }

    /**
     * Generates temporary credentials for external clients
     * 
     * @return {@link SessionCredentials} object
     */
    public Optional<SessionCredentials> getTemporarySessionCredentials() {
        StorageProviderAdapter storageProviderAdapter = getAdapterForIntegration(null);
        return storageProviderAdapter.getTemporarySessionCredentials(storageProviderTokenExpiration);
    }

}
