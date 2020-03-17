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

import com.zebrunner.reporting.domain.db.Test;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.domain.dto.CertificationType;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.util.DateTimeUtil;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.TEST_RUN_NOT_FOUND;

@Component
public class CertificationService {

    private static final String ERR_MSG_TEST_RUN_NOT_FOUND = "Test run with id %s can not be found";

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
            String platformName = testRun.getConfig().buildPlatformName();
            insertIntoCertification(certification, testRun.getId(), platformName);
        });
        return certification;
    }


    private void insertIntoCertification(CertificationType certification, Long testRunId, String platform) {
        TestRun testRun = testRunService.getTestRunById(testRunId);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, testRunId);
        }
        List<Test> tests = testService.getTestsByTestRunId(testRunId);
        tests.forEach(test -> {
            String correlationId = testRun.getCiRunId() + "_" + test.getCiTestId();
            String[] indices = constructIndexNames(test.getStartTime(), test.getFinishTime());
            Map<String, String> screenshotsInfo = elasticsearchService.getScreenshotsInfo(correlationId, indices);
            screenshotsInfo.keySet()
                           .forEach(key -> certification.addScreenshot(screenshotsInfo.get(key), platform, key));
        });
    }

    private String[] constructIndexNames(Date... dates) {
        return Arrays.stream(dates)
                     .map(date -> "logs-" + DateTimeUtil.toLocalDate(date).format(FORMATTER))
                     .toArray(String[]::new);
    }
}
