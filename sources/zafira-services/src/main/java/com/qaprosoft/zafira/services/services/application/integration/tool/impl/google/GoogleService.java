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
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.integration.tool.impl.google;

import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.AbstractIntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.google.GoogleServiceAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleService extends AbstractIntegrationService<GoogleServiceAdapter> {

    private final long googleTokenExpiration;

    public GoogleService(IntegrationService integrationService,
                         @Value("${google-token-expiration}") long googleTokenExpiration
    ) {
        super(integrationService, "GOOGLE");
        this.googleTokenExpiration = googleTokenExpiration;
    }

    public String getTemporaryAccessToken() throws IOException {
        GoogleServiceAdapter googleAdapter = getAdapterForIntegration(null);
        return googleAdapter.getTemporaryAccessToken(googleTokenExpiration);
    }

    /**
     * Throws an integration exception if integration is not configured
     * 
     * @return google drive service client
     */
    public GoogleDriveService getDriveService() {
        GoogleServiceAdapter googleAdapter = getAdapterForIntegration(null);
        return googleAdapter.getDriveService();
    }

    /**
     * Throws an integration exception if integration is not configured
     * 
     * @return google drive service client
     */
    public GoogleSpreadsheetsService getSpreadsheetsService() {
        GoogleServiceAdapter googleAdapter = getAdapterForIntegration(null);
        return googleAdapter.getSpreadsheetsService();
    }
}
