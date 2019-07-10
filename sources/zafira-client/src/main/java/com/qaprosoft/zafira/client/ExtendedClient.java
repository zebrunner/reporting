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

import com.qaprosoft.zafira.config.CiConfig;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.TagType;
import com.qaprosoft.zafira.models.dto.TestCaseType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestSuiteType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.dto.user.UserType;

import java.util.List;
import java.util.Set;

public interface ExtendedClient {

    /**
     * Registers user in Zafira, it may be a new one or existing returned by service.
     * @param userName - in general LDAP user name
     * @param email - user email
     * @param firstName - user first name
     * @param lastName - user last name
     * @return registered user
     */
    UserType registerUser(String userName, String email, String firstName, String lastName);

    /**
     * Registers test case in Zafira, it may be a new one or existing returned by service.
     * @param suiteId - test suite id
     * @param primaryOwnerId - primary owner user id
     * @param secondaryOwnerId - secondary owner user id
     * @param testClass - test class name
     * @param testMethod - test method name
     * @return registered test case
     */
    TestCaseType registerTestCase(Long suiteId, Long primaryOwnerId, Long secondaryOwnerId, String testClass, String testMethod);

    /**
     * Registers test work items.
     * @param testId - test id
     * @param workItems - test work items
     * @return test for which we registers work items
     */
    TestType registerWorkItems(Long testId, List<String> workItems);

    /**
     * Registers test suite in Zafira, it may be a new one or existing returned by service.
     * @param suiteName - test suite name
     * @param fileName - TestNG xml file name
     * @param userId - suite owner user id
     * @return created test suite
     */
    TestSuiteType registerTestSuite(String suiteName, String fileName, Long userId);


    /**
     * Registers job in Zafira, it may be a new one or existing returned by service.
     * @param jobUrl - CI job URL
     * @param userId - job owner user id
     * @return created job
     */
    JobType registerJob(String jobUrl, Long userId);

    /**
     * Registers new test run triggered by human.
     * @param testSuiteId - test suited id
     * @param userId - user id
     * @param configXML - test config XML
     * @param jobId - job id
     * @param ciConfig - ci config
     * @param startedBy - user id who started the suite
     * @param workItem - test work item
     * @return created test run
     */
    TestRunType registerTestRunByHUMAN(Long testSuiteId, Long userId, String configXML, Long jobId, CiConfig ciConfig, TestRun.Initiator startedBy, String workItem);

    /**
     * Registers new test run triggered by scheduler.
     * @param testSuiteId - test suited id
     * @param configXML - test config XML
     * @param jobId - job id
     * @param ciConfig - ci config
     * @param startedBy - user id who started the suite
     * @param workItem - test work item
     * @return created test run
     */
    TestRunType registerTestRunBySCHEDULER(Long testSuiteId, String configXML, Long jobId, CiConfig ciConfig, TestRun.Initiator startedBy, String workItem);

    /**
     * Registers new test run triggered by upstream job.
     * @param testSuiteId - test suited id
     * @param configXML - test config XML
     * @param jobId - job id
     * @param parentJobId - parent job id
     * @param ciConfig - ci config
     * @param startedBy - user id who started the suite
     * @param workItem - test work item
     * @return created test run
     */
    TestRunType registerTestRunUPSTREAM_JOB(Long testSuiteId, String configXML, Long jobId, Long parentJobId, CiConfig ciConfig, TestRun.Initiator startedBy, String workItem);

    /**
     * Finalizes test run calculating test results.
     * @param testRun - test run object
     * @return updated test run
     */
    TestRunType registerTestRunResults(TestRunType testRun);

    /**
     * Registers test run in Zafira.
     * @param name - test name
     * @param group - test group
     * @param status - test status
     * @param testArgs - test args
     * @param testRunId - test run id
     * @param testCaseId - test case id
     * @param retry - retry count
     * @param dependsOnMethods - list of dependent tests
     * @param configXML - config XML
     * @return registered test
     */
    TestType registerTestStart(String name, String group, Status status, String testArgs, Long testRunId, Long testCaseId, int retry,
                               String configXML, String[] dependsOnMethods, String ciTestId, Set<TagType> tags);

    /**
     * Registers test re-run in Zafira.
     * @param test - test object
     * @return registered test
     */
    TestType registerTestRestart(TestType test);

}
