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
package com.qaprosoft.zafira.services.services.application.integration.impl.google.auth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.sheets.v4.Sheets;
import com.qaprosoft.zafira.services.services.application.integration.impl.google.AbstractGoogleService;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleSheetsAuthService extends AbstractGoogleService {

    public static Sheets getService(byte[] credsFile) throws IOException {
        return getService(credsFile, null);
    }

    public static Sheets getService(byte[] credsFile, Long expirationTime) throws IOException {
        return new Sheets.Builder(getHttpTransport(), getJsonFactory(), getCredentialInstance(credsFile, expirationTime))
                .setApplicationName(getApplicationName())
                .build();
    }

    public static Credential getCredentialInstance(byte[] credsFile, Long expirationTime) throws IOException {
        return expirationTime == null ? authorize(credsFile) : authorize(credsFile, expirationTime);
    }

}
