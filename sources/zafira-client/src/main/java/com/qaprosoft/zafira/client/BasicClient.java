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

import com.qaprosoft.zafira.client.impl.ZafiraClientImpl;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.ProjectType;
import com.qaprosoft.zafira.models.dto.TestArtifactType;
import com.qaprosoft.zafira.models.dto.TestCaseType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestSuiteType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.dto.auth.AccessTokenType;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;
import com.qaprosoft.zafira.models.dto.auth.TenantType;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.util.http.HttpClient;

import java.util.HashMap;
import java.util.List;

public interface BasicClient {

    void setAuthToken(String authToken);

    boolean isAvailable();

    HttpClient.Response<UserType> getUserProfile();

    HttpClient.Response<UserType> getUserProfile(String username);

    HttpClient.Response<AuthTokenType> login(String username, String password);

    HttpClient.Response<AccessTokenType> generateAccessToken();

    HttpClient.Response<UserType> createUser(UserType user);

    HttpClient.Response<AuthTokenType> refreshToken(String token);

    HttpClient.Response<JobType> createJob(JobType job);

    HttpClient.Response<TestSuiteType> createTestSuite(TestSuiteType testSuite);

    HttpClient.Response<TestRunType> startTestRun(TestRunType testRun);

    HttpClient.Response<TestRunType> updateTestRun(TestRunType testRun);

    HttpClient.Response<TestRunType> finishTestRun(long id);

    HttpClient.Response<TestRunType> getTestRun(long id);

    HttpClient.Response<TestRunType> getTestRunByCiRunId(String ciRunId);

    HttpClient.Response<TestType> startTest(TestType test);

    HttpClient.Response<TestType> finishTest(TestType test);

    void deleteTest(long id);

    HttpClient.Response<TestType> createTestWorkItems(long testId, List<String> workItems);

    /**
     * Attaches test artifact like logs or demo URLs.
     * @param artifact - test artifact
     */
    void addTestArtifact(TestArtifactType artifact);

    HttpClient.Response<TestCaseType> createTestCase(TestCaseType testCase);

    HttpClient.Response<TestCaseType[]> createTestCases(TestCaseType[] testCases);

    HttpClient.Response<TestType[]> getTestRunResults(long id);

    /**
     * Aborts test run.
     * @param id of test run
     * @return status
     */
    boolean abortTestRun(long id);

    /**
     * Gets project by name
     * @param name of the project
     * @return project
     */
    HttpClient.Response<ProjectType> getProjectByName(String name);

    String getProject();

    /**
     * Initializes project context, sets default project if none found in DB.
     * @param project name
     * @return instance of {@link ZafiraClientImpl}
     */
    BasicClient initProject(String project);

    HttpClient.Response<List<HashMap<String, String>>> getToolSettings(String tool, boolean decrypt);

    /**
     * Returns user by username or anonymous if not found.
     * @param username to find user
     * @return user from DB
     */
    UserType getUserOrAnonymousIfNotFound(String username);

    String getServiceUrl();

    String getRealServiceUrl();

    TenantType getTenantType();

    String getAuthToken();

}
