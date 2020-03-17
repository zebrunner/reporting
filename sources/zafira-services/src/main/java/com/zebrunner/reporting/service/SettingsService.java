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
package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.SettingsMapper;
import com.zebrunner.reporting.domain.db.Setting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsService {

    private final SettingsMapper settingsMapper;

    public SettingsService(SettingsMapper settingsMapper) {
        this.settingsMapper = settingsMapper;
    }

    @Transactional(readOnly = true)
    public Setting getSettingByName(String name) {
        return settingsMapper.getSettingByName(name);
    }

    @Transactional(rollbackFor = Exception.class)
    public Setting updateSetting(Setting setting) {
        //setting.setValue(StringUtils.isBlank(setting.getValue() != null ? setting.getValue().trim() : null) ? null : setting.getValue());
        settingsMapper.updateSetting(setting);
        return setting;
    }

    @Transactional(readOnly = true)
    public String getPostgresVersion() {
        return settingsMapper.getPostgresVersion();
    }

}
