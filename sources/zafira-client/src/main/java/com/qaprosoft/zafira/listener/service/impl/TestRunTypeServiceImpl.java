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
package com.qaprosoft.zafira.listener.service.impl;

import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.config.CiConfig;
import com.qaprosoft.zafira.listener.service.TestRunTypeService;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestType;
import com.qaprosoft.zafira.models.dto.config.ConfigurationType;
import com.qaprosoft.zafira.util.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class TestRunTypeServiceImpl implements TestRunTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunTypeServiceImpl.class);

    private final ZafiraClient zafiraClient;

    public TestRunTypeServiceImpl(ZafiraClient zafiraClient) {
        this.zafiraClient = zafiraClient;
    }

    @Override
    public TestRunType findTestRunByCiRunId(String ciRunId) {
        HttpClient.Response<TestRunType> response = zafiraClient.getTestRunByCiRunId(ciRunId);
        return response.getObject();
    }

    @Override
    public TestRunType startTestRun(TestRunType testRun) {
        HttpClient.Response<TestRunType> response = zafiraClient.startTestRun(testRun);
        return response.getObject();
    }

    @Override
    public TestRunType rerun(TestRunType testRun, int ciBuildNumber, long suiteId, ConfigurationType configuration) {
        testRun.setBuildNumber(ciBuildNumber);
        testRun.setConfigXML(convertConfigurationToXML(configuration));
        testRun.setTestSuiteId(suiteId);
        return startTestRun(testRun);
    }

    @Override
    public boolean abort(Long testRunId) {
        return zafiraClient.abortTestRun(testRunId);
    }

    @Override
    public List<TestType> findTestRunResults(long id) {
        return Arrays.asList(zafiraClient.getTestRunResults(id).getObject());
    }

    @Override
    public TestRunType register(TestRunType testRun, CiConfig.BuildCase buildCase, long suiteId, long jobId, long userId, JobType parentJob,
                                CiConfig ciConfig, String jiraSuiteId, ConfigurationType configuration) {
        TestRunType result;
        switch (buildCase) {
            case UPSTREAMTRIGGER:
                result = registerTestRunUPSTREAM_JOB(suiteId, jobId, parentJob.getId(), ciConfig, jiraSuiteId, configuration);
                break;
            case TIMERTRIGGER:
            case SCMTRIGGER:
                result = registerTestRunBySCHEDULER(suiteId, jobId, ciConfig, jiraSuiteId, configuration);
                break;
            case MANUALTRIGGER:
                result = registerTestRunByHUMAN(suiteId, jobId, userId, ciConfig, jiraSuiteId, configuration);
                break;
            default:
                throw new RuntimeException("Unable to register test run for zafira service: " + zafiraClient.getServiceUrl() +
                        " due to the misses build cause: '" + buildCase + "'");
        }
        return result;
    }

    private TestRunType registerTestRunUPSTREAM_JOB(long suiteId, long jobId, long parentJobId, CiConfig ciConfig, String jiraSuiteId, ConfigurationType configuration) {
        return zafiraClient.registerTestRunUPSTREAM_JOB(suiteId, convertConfigurationToXML(configuration), jobId, parentJobId,
                ciConfig, TestRun.Initiator.UPSTREAM_JOB, jiraSuiteId);
    }

    private TestRunType registerTestRunBySCHEDULER(long suiteId, long jobId, CiConfig ciConfig, String jiraSuiteId, ConfigurationType configuration) {
        return zafiraClient.registerTestRunBySCHEDULER(suiteId, convertConfigurationToXML(configuration), jobId, ciConfig,
                TestRun.Initiator.SCHEDULER, jiraSuiteId);
    }

    private TestRunType registerTestRunByHUMAN(long suiteId, long jobId, long userId, CiConfig ciConfig, String jiraSuiteId, ConfigurationType configuration) {
        return zafiraClient.registerTestRunByHUMAN(suiteId, userId, convertConfigurationToXML(configuration), jobId, ciConfig,
                TestRun.Initiator.HUMAN, jiraSuiteId);
    }

    @Override
    public TestRunType registerTestRunResults(TestRunType testRun, ConfigurationType configuration) {
        testRun.setConfigXML(convertConfigurationToXML(configuration));
        return zafiraClient.registerTestRunResults(testRun);
    }

    /**
     * Marshals configuration bean to XML.
     *
     * @param config bean
     * @return XML representation of configuration bean
     */
    public String convertConfigurationToXML(ConfigurationType config) {
        final StringWriter writer = new StringWriter();
        try {
            Marshaller marshaller = JAXBContext.newInstance(ConfigurationType.class).createMarshaller();
            marshaller.marshal(config != null ? config : new ConfigurationType(), writer);
        } catch (Exception e) {
            LOGGER.error("Unable to convert config to XML!", e);
        }
        return writer.toString();
    }

}
