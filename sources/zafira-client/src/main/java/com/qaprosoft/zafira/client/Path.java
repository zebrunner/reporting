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
package com.qaprosoft.zafira.client;

public enum Path {

    STATUS_PATH("/api/status"),
    PROFILE_PATH("/api/users/profile"),
    LOGIN_PATH("/api/auth/login"),
    ACCESS_PATH("/api/auth/access"),
    REFRESH_TOKEN_PATH("/api/auth/refresh"),
    USERS_PATH("/api/users"),
    JOBS_PATH("/api/jobs"),
    TESTS_PATH("/api/tests"),
    TEST_FINISH_PATH("/api/tests/%d/finish"),
    TEST_BY_ID_PATH("/api/tests/%d"),
    TEST_WORK_ITEMS_PATH("/api/tests/%d/workitems"),
    TEST_ARTIFACTS_PATH("/api/tests/%d/artifacts"),
    TEST_SUITES_PATH("/api/tests/suites"),
    TEST_CASES_PATH("/api/tests/cases"),
    TEST_CASES_BATCH_PATH("/api/tests/cases/batch"),
    TEST_RUNS_PATH("/api/tests/runs"),
    TEST_RUNS_FINISH_PATH("/api/tests/runs/%d/finish"),
    TEST_RUNS_RESULTS_PATH("/api/tests/runs/%d/results"),
    TEST_RUNS_ABORT_PATH("/api/tests/runs/abort"),
    TEST_RUN_BY_ID_PATH("/api/tests/runs/%d"),
    SETTINGS_TOOL_PATH("/api/settings/tool/%s"),
    AMAZON_SESSION_CREDENTIALS_PATH("/api/settings/amazon/creds"),
    GOOGLE_SESSION_CREDENTIALS_PATH("/api/settings/google/creds"),
    TENANT_TYPE_PATH("/api/auth/tenant"),
    PROJECTS_PATH("/api/projects/%s"),

    DEVICES_PATH("/api/v1/devices"),
    DEVICES_ITEM_PATH("/api/v1/devices/%s"),
    USER_DEVICES_PATH("/api/v1/user/devices"),
    USER_DEVICES_BY_ID_PATH("/api/v1/user/devices/%s"),
    USER_DEVICES_REMOTE_CONNECT_PATH("/api/v1/user/devices/%s/remoteConnect");
    
    private final String relativePath;

    Path(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String build(String serviceUrl, Object... parameters) {
        return serviceUrl + String.format(relativePath, parameters);
    }
    
}
