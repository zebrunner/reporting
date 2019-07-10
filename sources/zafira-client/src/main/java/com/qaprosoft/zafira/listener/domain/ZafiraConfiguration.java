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
package com.qaprosoft.zafira.listener.domain;

import org.apache.commons.lang3.StringUtils;

public enum ZafiraConfiguration implements Configuration {

    ENABLED("zafira_enabled", false, Boolean.class),
    SERVICE_URL("zafira_service_url", StringUtils.EMPTY, String.class),
    ACCESS_TOKEN("zafira_access_token", StringUtils.EMPTY, String.class),
    PROJECT("zafira_project", StringUtils.EMPTY, String.class, true),
    RERUN_FAILURES("zafira_rerun_failures", false, Boolean.class),
    CONFIGURATOR("zafira_configurator", "com.qaprosoft.zafira.listener.DefaultConfigurator", String.class, true);

    private final String configName;
    private final Object defaultValue;
    private final Class configurationClass;
    private final boolean canOverride;

    ZafiraConfiguration(String configName, Object defaultValue, Class configurationClass) {
        this(configName, defaultValue, configurationClass, false);
    }

    ZafiraConfiguration(String configName, Object defaultValue, Class configurationClass, boolean canOverride) {
        this.configName = configName;
        this.defaultValue = defaultValue;
        this.configurationClass = configurationClass;
        this.canOverride = canOverride;
    }

    @Override
    public boolean canOverride() {
        return canOverride;
    }

    @Override
    public String getConfigName() {
        return configName;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Class getConfigClass() {
        return configurationClass;
    }

}
