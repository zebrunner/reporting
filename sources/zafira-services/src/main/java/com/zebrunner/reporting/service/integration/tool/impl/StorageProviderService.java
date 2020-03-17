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
package com.zebrunner.reporting.service.integration.tool.impl;

import com.zebrunner.reporting.domain.dto.aws.FileUploadType;
import com.zebrunner.reporting.domain.dto.aws.SessionCredentials;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.AbstractIntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.storageprovider.StorageProviderAdapter;
import com.zebrunner.reporting.service.integration.tool.proxy.StorageProviderProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StorageProviderService extends AbstractIntegrationService<StorageProviderAdapter> {

    private final int storageProviderTokenExpiration;

    public StorageProviderService(
            IntegrationService integrationService,
            StorageProviderProxy storageProviderProxy,
            @Value("${amazon-token-expiration}") int storageProviderTokenExpiration
    ) {
        super(integrationService, storageProviderProxy, "AMAZON");
        this.storageProviderTokenExpiration = storageProviderTokenExpiration;
    }

    public String saveFile(final FileUploadType file) {
        StorageProviderAdapter adapter = getAdapterByIntegrationId(null);
        return adapter.saveFile(file);
    }

    public void removeFile(final String linkToFile) {
        StorageProviderAdapter adapter = getAdapterByIntegrationId(null);
        adapter.removeFile(linkToFile);
    }

    public Optional<SessionCredentials> getTemporarySessionCredentials() {
        StorageProviderAdapter adapter = getAdapterByIntegrationId(null);
        return adapter.getTemporarySessionCredentials(storageProviderTokenExpiration);
    }

}
