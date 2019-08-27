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

import com.qaprosoft.zafira.services.services.application.integration.tool.AbstractIntegration;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.google.GoogleServiceAdapter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleService extends AbstractIntegration<GoogleServiceAdapter> {

    public GoogleService() {
        super("GOOGLE");
    }

    public String getTemporaryAccessToken(Long expiresIn) throws IOException {
        GoogleServiceAdapter googleAdapter = getDefaultAdapter();
        return googleAdapter.getTemporaryAccessToken(expiresIn);
    }

    /**
     * Throws an integration exception if integration is not configured
     * 
     * @return google drive service client
     */
    public GoogleDriveService getDriveService() {
        GoogleServiceAdapter googleAdapter = getDefaultAdapter();
        return googleAdapter.getDriveService();
    }

    /**
     * Throws an integration exception if integration is not configured
     * 
     * @return google drive service client
     */
    public GoogleSpreadsheetsService getSpreadsheetsService() {
        GoogleServiceAdapter googleAdapter = getDefaultAdapter();
        return googleAdapter.getSpreadsheetsService();
    }
}
