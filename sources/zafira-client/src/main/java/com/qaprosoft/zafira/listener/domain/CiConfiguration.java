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

import java.util.UUID;

public enum CiConfiguration implements Configuration {

    CI_RUN_ID("ci_run_id", UUID.randomUUID().toString(), String.class),
    CI_URL("ci_url", "http://localhost:8080/job/unavailable", String.class),
    CI_BUILD("ci_build", null, Integer.class),
    CI_BUILD_CASE("ci_build_cause", "MANUALTRIGGER", String.class),
    CI_PARENT_URL("ci_parent_url", null, String.class),
    CI_PARENT_BUILD("ci_parent_build", null, Integer.class),

    GIT_BRANCH("git_branch", null, String.class),
    GIT_COMMIT("git_commit", null, String.class),
    GIT_URL("git_url", null, String.class),

    JIRA_SUITE_ID("jira_suite_id", null, Integer.class);

    private final String configName;
    private final Object defaultValue;
    private final Class configurationClass;
    private final boolean canOverride;

    CiConfiguration(String configName, Object defaultValue, Class configurationClass) {
        this(configName, defaultValue, configurationClass, false);
    }

    CiConfiguration(String configName, Object defaultValue, Class configurationClass, boolean canOverride) {
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
