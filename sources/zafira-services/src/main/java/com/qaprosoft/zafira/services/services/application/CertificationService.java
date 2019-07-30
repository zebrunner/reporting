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
package com.qaprosoft.zafira.services.services.application;

import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.dto.CertificationType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.integration.impl.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class CertificationService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private final ElasticsearchService elasticsearchService;
    private final TestRunService testRunService;
    private final TestService testService;

    public CertificationService(ElasticsearchService elasticsearchService, TestRunService testRunService, TestService testService) {
        this.elasticsearchService = elasticsearchService;
        this.testRunService = testRunService;
        this.testService = testService;
    }

    public CertificationType getCertificationDetails(Long upstreamJobId, Integer upstreamJobBuildNumber) {
        if (!elasticsearchService.isClientInitialized()) {
            return null;
        }
        CertificationType certification = new CertificationType();
        List<TestRun> testRuns = testRunService.getTestRunsByUpstreamJobIdAndUpstreamJobBuildNumber(upstreamJobId, upstreamJobBuildNumber);
        testRuns.forEach(testRun -> {
            String platformName = buildPlatformName(testRun);
            insertIntoCertification(certification, testRun.getId(), platformName);
        });
        return certification;
    }

    private String buildPlatformName(TestRun testRun){
        StringBuilder platform = new StringBuilder(testRun.getPlatform());
        String browserVersion = testRun.getConfig().getBrowserVersion();
        if(!"*".equals(browserVersion)) {
            platform.append(" ").append(browserVersion);
        }
        return platform.toString();
    }

    private void insertIntoCertification(CertificationType certification, Long testRunId, String platform) {
        TestRun testRun = testRunService.getTestRunById(testRunId);
        if (testRun == null) {
            throw new ServiceException("Test run with id " + testRunId + " not found");
        }
        List<Test> tests = testService.getTestsByTestRunId(testRunId);
        tests.forEach(test -> {
            String correlationId = testRun.getCiRunId() + "_" + test.getCiTestId();
            String[] indices = buildIndices(test.getStartTime(), test.getFinishTime());
            Map<String, String> screenshotsInfo = elasticsearchService.getScreenshotsInfo(correlationId, indices);
            screenshotsInfo.keySet()
                           .forEach(key -> certification.addScreenshot(screenshotsInfo.get(key), platform, key));
        });
    }

    private String[] buildIndices(Date... dates) {
        return Arrays.stream(dates)
                     .map(date -> "logs-" + date.toInstant()
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDate()
                                                .format(FORMATTER))
                     .toArray(String[]::new);
    }
}
