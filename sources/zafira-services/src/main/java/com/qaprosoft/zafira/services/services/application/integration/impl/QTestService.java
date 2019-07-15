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
package com.qaprosoft.zafira.services.services.application.integration.impl;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import com.qaprosoft.zafira.services.services.application.integration.context.QTestContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.qaprosoft.zafira.models.db.Setting.Tool.QTEST;

@Component
public class QTestService extends AbstractIntegration<QTestContext> {

    private final SettingsService settingsService;

    public QTestService(SettingsService settingsService) {
        super(settingsService, QTEST, QTestContext.class);
        this.settingsService = settingsService;
    }

    @Override
    public boolean isConnected() {
        return mapContext(context -> {
            boolean result = false;
            List<Setting> settings = settingsService.getSettingsByTool(QTEST);
            Setting setting = settings.stream()
                                      .filter(qTestSetting -> qTestSetting.getName().equals("QTEST_URL"))
                                      .findAny()
                                      .orElse(null);
            if (setting != null && StringUtils.isNotEmpty(setting.getValue())){
                result = true;
            }
            return result;
        }).orElse(false);
    }

}
