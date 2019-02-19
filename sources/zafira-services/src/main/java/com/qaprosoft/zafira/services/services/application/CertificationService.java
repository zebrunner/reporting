/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import com.qaprosoft.zafira.services.services.application.jmx.ElasticsearchService;
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

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private TestRunService testRunService;

    @Autowired
    private TestService testService;

    public CertificationType insertIntoCertification(CertificationType certification, Long testRunId, String platform) throws ServiceException {
        TestRun testRun = testRunService.getTestRunById(testRunId);
        if(testRun == null) {
            throw new ServiceException("Test run with id " + testRunId + " not found");
        }
        List<Test> tests = testService.getTestsByTestRunId(testRunId);
        tests.forEach(test -> {
            Map<String, String> screenshotsInfo = elasticsearchService.getScreenshotsInfo(testRun.getCiRunId() + "_" +
                    test.getCiTestId(), buildIndices(test.getStartTime(), test.getFinishTime()));
            screenshotsInfo.keySet().forEach(key -> certification.addScreenshot(screenshotsInfo.get(key), platform, key));
        });
        return certification;
    }

    private String[] buildIndices(Date... dates) {
        return Arrays.stream(dates).map(date -> "logs-" + date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                                              .format(FORMATTER)).toArray(String[]::new);
    }
}
