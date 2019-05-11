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
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractContext {

    protected static final Logger LOGGER = Logger.getLogger(AbstractContext.class);

    private Map<Setting.SettingType, Object> settings;
    private final Boolean enabled;

    public AbstractContext(Map<Setting.SettingType, String> settings, String enabled) {
        this(settings, Boolean.valueOf(enabled));
    }

    public AbstractContext(Map<Setting.SettingType, String> settings, Boolean enabled) {
        if (settings != null) {
            this.settings = new HashMap<>(settings);
        }
        this.enabled = enabled;
    }

    public void setSettings(Map<Setting.SettingType, Object> settings) {
        this.settings = settings;
    }

    public Map<Setting.SettingType, Object> getSettings() {
        return settings;
    }

    public Boolean isEnabled() {
        return enabled;
    }

}
