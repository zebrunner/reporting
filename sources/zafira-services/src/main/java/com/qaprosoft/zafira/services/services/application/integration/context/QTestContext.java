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

import java.util.Map;

public class QTestContext extends AbstractContext {

    private final String qTestUrl;

    public QTestContext(Map<Setting.SettingType, String> settings) {
        super(settings, settings.get(Setting.SettingType.QTEST_ENABLED));

        this.qTestUrl = settings.get(Setting.SettingType.QTEST_URL);
    }

    public String getQTestUrl() {
        return qTestUrl;
    }

}
