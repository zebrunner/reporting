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
package com.qaprosoft.zafira.services.services.application.integration.tool.adapter.google;

import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.services.services.application.integration.tool.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.adapter.AdapterParam;
import com.qaprosoft.zafira.services.services.application.integration.tool.impl.google.AbstractGoogleService;
import com.qaprosoft.zafira.services.services.application.integration.tool.impl.google.GoogleDriveService;
import com.qaprosoft.zafira.services.services.application.integration.tool.impl.google.GoogleSpreadsheetsService;
import com.qaprosoft.zafira.services.services.application.integration.tool.impl.google.auth.GoogleDriveAuthService;
import com.qaprosoft.zafira.services.services.application.integration.tool.impl.google.auth.GoogleSheetsAuthService;

import java.io.IOException;

public class GoogleIntegrationAdapter extends AbstractIntegrationAdapter implements GoogleServiceAdapter {

    private final byte[] credsFile;
    private final String credsFileOriginName;

    private final GoogleDriveService driveService;
    private final GoogleSpreadsheetsService spreadsheetsService;

    public GoogleIntegrationAdapter(Integration integration) {
        super(integration);

        this.credsFile = getAttributeBinaryData(integration, GoogleParam.GOOGLE_CLIENT_SECRET_ORIGIN);
        this.credsFileOriginName = getAttributeValue(integration, GoogleParam.GOOGLE_CLIENT_SECRET_ORIGIN);

        this.driveService = new GoogleDriveService(credsFile);
        this.spreadsheetsService = new GoogleSpreadsheetsService(credsFile);
    }

    private enum GoogleParam implements AdapterParam {
        GOOGLE_CLIENT_SECRET_ORIGIN("GOOGLE_CLIENT_SECRET_ORIGIN");

        private final String name;

        GoogleParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public byte[] getCredsFile() {
        return credsFile;
    }

    public String getCredsFileOriginName() {
        return credsFileOriginName;
    }

    @Override
    public boolean isConnected() {
        try {
            GoogleDriveAuthService.getService(credsFile).about();
            GoogleSheetsAuthService.getService(credsFile).spreadsheets();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public String getTemporaryAccessToken(Long expiresIn) throws IOException {
        return credsFile != null ? AbstractGoogleService.authorize(credsFile, expiresIn).getAccessToken() : null;
    }

    @Override
    public GoogleDriveService getDriveService() {
        return driveService;
    }

    @Override
    public GoogleSpreadsheetsService getSpreadsheetsService() {
        return spreadsheetsService;
    }
}
