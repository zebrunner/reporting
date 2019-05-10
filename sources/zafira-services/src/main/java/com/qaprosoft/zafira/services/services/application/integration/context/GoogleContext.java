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
package com.qaprosoft.zafira.services.services.application.integration.context;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.application.integration.impl.google.GoogleDriveService;
import com.qaprosoft.zafira.services.services.application.integration.impl.google.GoogleSpreadsheetsService;

import java.util.HashMap;
import java.util.Map;

public class GoogleContext extends AbstractContext
{

    private final byte[] credsFile;
    private final String credsFileOriginName;

    private GoogleDriveService driveService;
    private GoogleSpreadsheetsService spreadsheetsService;

    public GoogleContext(Map<Setting.SettingType, Setting> settings) {
        super(null, settings.get(Setting.SettingType.GOOGLE_ENABLED).getValue());
        setSettings(new HashMap<>(settings));

        byte[] credsFile = settings.get(Setting.SettingType.GOOGLE_CLIENT_SECRET_ORIGIN).getFile();
        String credsFileOriginName = settings.get(Setting.SettingType.GOOGLE_CLIENT_SECRET_ORIGIN).getValue();

        this.credsFile = credsFile;
        this.credsFileOriginName = credsFileOriginName;
        this.driveService = new GoogleDriveService(credsFile);
        this.spreadsheetsService = new GoogleSpreadsheetsService(credsFile);
    }

    public byte[] getCredsFile() {
        return credsFile;
    }

    public String getCredsFileOriginName() {
        return credsFileOriginName;
    }

    public GoogleDriveService getDriveService() {
        return driveService;
    }

    public GoogleSpreadsheetsService getSpreadsheetsService() {
        return spreadsheetsService;
    }
}
