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
package com.qaprosoft.zafira.client.impl;

import com.google.api.services.sheets.v4.Sheets;
import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.client.ExtendedClient;
import com.qaprosoft.zafira.client.BasicClient;
import com.qaprosoft.zafira.client.IntegrationClient;
import com.qaprosoft.zafira.config.CIConfig;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.ProjectType;
import com.qaprosoft.zafira.models.dto.TagType;
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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ZafiraClientImpl implements ZafiraClient {

    private final BasicClient basicClient;
    private final ExtendedClient extendedClient;
    private final IntegrationClient integrationClient;

    public ZafiraClientImpl(String serviceUrl) {
        this.basicClient = new BasicClientImpl(serviceUrl);
        this.extendedClient = new ExtendedClientImpl(this.basicClient);
        this.integrationClient = new IntegrationClientImpl(this.basicClient);
    }

    @Override
    public void setAuthToken(String authToken) {
        basicClient.setAuthToken(authToken);
    }

    @Override
    public boolean isAvailable() {
        return basicClient.isAvailable();
    }

    @Override
    public HttpClient.Response<UserType> getUserProfile() {
        return basicClient.getUserProfile();
    }

    @Override
    public HttpClient.Response<UserType> getUserProfile(String username) {
        return basicClient.getUserProfile(username);
    }

    @Override
    public HttpClient.Response<AuthTokenType> login(String username, String password) {
        return basicClient.login(username, password);
    }

    @Override
    public HttpClient.Response<AccessTokenType> generateAccessToken() {
        return basicClient.generateAccessToken();
    }

    @Override
    public HttpClient.Response<UserType> createUser(UserType user) {
        return basicClient.createUser(user);
    }

    @Override
    public HttpClient.Response<AuthTokenType> refreshToken(String token) {
        return basicClient.refreshToken(token);
    }

    @Override
    public HttpClient.Response<JobType> createJob(JobType job) {
        return basicClient.createJob(job);
    }

    @Override
    public HttpClient.Response<TestSuiteType> createTestSuite(TestSuiteType testSuite) {
        return basicClient.createTestSuite(testSuite);
    }

    @Override
    public HttpClient.Response<TestRunType> startTestRun(TestRunType testRun) {
        return basicClient.startTestRun(testRun);
    }

    @Override
    public HttpClient.Response<TestRunType> updateTestRun(TestRunType testRun) {
        return basicClient.updateTestRun(testRun);
    }

    @Override
    public HttpClient.Response<TestRunType> finishTestRun(long id) {
        return basicClient.finishTestRun(id);
    }

    @Override
    public HttpClient.Response<TestRunType> getTestRun(long id) {
        return basicClient.getTestRun(id);
    }

    @Override
    public HttpClient.Response<TestRunType> getTestRunByCiRunId(String ciRunId) {
        return basicClient.getTestRunByCiRunId(ciRunId);
    }

    @Override
    public HttpClient.Response<TestType> startTest(TestType test) {
        return basicClient.startTest(test);
    }

    @Override
    public HttpClient.Response<TestType> finishTest(TestType test) {
        return basicClient.finishTest(test);
    }

    @Override
    public void deleteTest(long id) {
        basicClient.deleteTest(id);
    }

    @Override
    public HttpClient.Response<TestType> createTestWorkItems(long testId, List<String> workItems) {
        return basicClient.createTestWorkItems(testId, workItems);
    }

    @Override
    public void addTestArtifact(TestArtifactType artifact) {
        basicClient.addTestArtifact(artifact);
    }

    @Override
    public HttpClient.Response<TestCaseType> createTestCase(TestCaseType testCase) {
        return basicClient.createTestCase(testCase);
    }

    @Override
    public HttpClient.Response<TestCaseType[]> createTestCases(TestCaseType[] testCases) {
        return basicClient.createTestCases(testCases);
    }

    @Override
    public HttpClient.Response<TestType[]> getTestRunResults(long id) {
        return basicClient.getTestRunResults(id);
    }

    @Override
    public boolean abortTestRun(long id) {
        return basicClient.abortTestRun(id);
    }

    @Override
    public HttpClient.Response<ProjectType> getProjectByName(String name) {
        return basicClient.getProjectByName(name);
    }

    @Override
    public String getProject() {
        return basicClient.getProject();
    }

    @Override
    public BasicClient initProject(String project) {
        return basicClient.initProject(project);
    }

    @Override
    public HttpClient.Response<List<HashMap<String, String>>> getToolSettings(String tool, boolean decrypt) {
        return basicClient.getToolSettings(tool, decrypt);
    }

    @Override
    public UserType getUserOrAnonymousIfNotFound(String username) {
        return basicClient.getUserOrAnonymousIfNotFound(username);
    }

    @Override
    public String getServiceURL() {
        return basicClient.getServiceURL();
    }

    @Override
    public TenantType getTenantType() {
        return basicClient.getTenantType();
    }

    @Override
    public String getAuthToken() {
        return basicClient.getAuthToken();
    }

    @Override
    public UserType registerUser(String userName, String email, String firstName, String lastName) {
        return extendedClient.registerUser(userName, email, firstName, lastName);
    }

    @Override
    public TestCaseType registerTestCase(Long suiteId, Long primaryOwnerId, Long secondaryOwnerId, String testClass, String testMethod) {
        return extendedClient.registerTestCase(suiteId, primaryOwnerId, secondaryOwnerId, testClass, testMethod);
    }

    @Override
    public TestType registerWorkItems(Long testId, List<String> workItems) {
        return extendedClient.registerWorkItems(testId, workItems);
    }

    @Override
    public TestSuiteType registerTestSuite(String suiteName, String fileName, Long userId) {
        return extendedClient.registerTestSuite(suiteName, fileName, userId);
    }

    @Override
    public JobType registerJob(String jobUrl, Long userId) {
        return extendedClient.registerJob(jobUrl, userId);
    }

    @Override
    public TestRunType registerTestRunByHUMAN(Long testSuiteId, Long userId, String configXML, Long jobId, CIConfig ciConfig, TestRun.Initiator startedBy, String workItem) {
        return extendedClient.registerTestRunByHUMAN(testSuiteId, userId, configXML, jobId, ciConfig, startedBy, workItem);
    }

    @Override
    public TestRunType registerTestRunBySCHEDULER(Long testSuiteId, String configXML, Long jobId, CIConfig ciConfig, TestRun.Initiator startedBy, String workItem) {
        return extendedClient.registerTestRunBySCHEDULER(testSuiteId, configXML, jobId, ciConfig, startedBy, workItem);
    }

    @Override
    public TestRunType registerTestRunUPSTREAM_JOB(Long testSuiteId, String configXML, Long jobId, Long parentJobId, CIConfig ciConfig, TestRun.Initiator startedBy, String workItem) {
        return extendedClient.registerTestRunUPSTREAM_JOB(testSuiteId, configXML, jobId, parentJobId, ciConfig, startedBy, workItem);
    }

    @Override
    public TestRunType registerTestRunResults(TestRunType testRun) {
        return extendedClient.registerTestRunResults(testRun);
    }

    @Override
    public TestType registerTestStart(String name, String group, Status status, String testArgs, Long testRunId, Long testCaseId, int retry, String configXML, String[] dependsOnMethods, String ciTestId, Set<TagType> tags) {
        return extendedClient.registerTestStart(name, group, status, testArgs, testRunId, testCaseId, retry, configXML, dependsOnMethods, ciTestId, tags);
    }

    @Override
    public TestType registerTestRestart(TestType test) {
        return extendedClient.registerTestRestart(test);
    }

    @Override
    public String uploadFile(File file, Integer expiresIn, String keyPrefix) throws Exception {
        return integrationClient.uploadFile(file, expiresIn, keyPrefix);
    }

    @Override
    public Optional<Sheets> getSpreadsheetService() {
        return integrationClient.getSpreadsheetService();
    }

}
