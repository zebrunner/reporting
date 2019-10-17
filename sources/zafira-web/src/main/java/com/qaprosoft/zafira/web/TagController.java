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
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.config.Configuration;
import com.qaprosoft.zafira.models.dto.tag.IntegrationDataType;
import com.qaprosoft.zafira.models.dto.tag.IntegrationTag;
import com.qaprosoft.zafira.service.TagService;
import com.qaprosoft.zafira.service.TestRunService;
import com.qaprosoft.zafira.service.util.URLResolver;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Map;

import static com.qaprosoft.zafira.service.util.XmlConfigurationUtil.parseConfigToMap;
import static com.qaprosoft.zafira.service.util.XmlConfigurationUtil.readArguments;

@Api("Tags operations")
@RequestMapping(path = "api/tags", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TagController extends AbstractController {

    private final TagService tagService;
    private final TestRunService testRunService;
    private final URLResolver urlResolver;

    public TagController(TagService tagService, TestRunService testRunService, URLResolver urlResolver) {
        this.tagService = tagService;
        this.testRunService = testRunService;
        this.urlResolver = urlResolver;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get integration info", nickname = "getTestIntegrationInfo", httpMethod = "GET", response = IntegrationDataType.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/{ciRunId}/integration")
    public IntegrationDataType getIntegrationInfo(
            @PathVariable("ciRunId") String ciRunId,
            @RequestParam("integrationTag") IntegrationTag integrationTag
    ) {
        IntegrationDataType integrationData = new IntegrationDataType();
        TestRun testRun = testRunService.getTestRunByCiRunIdFull(ciRunId);

        if (testRun != null) {
            tagService.setTestInfoByIntegrationTag(ciRunId, integrationTag, integrationData);

            // finishedAt value generation based on startedAt & elapsed
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(testRun.getStartedAt());
            if (testRun.getElapsed() != null) {
                calendar.add(Calendar.SECOND, testRun.getElapsed());
            }

            integrationData.setFinishedAt(calendar.getTime());
            integrationData.setStartedAt(testRun.getStartedAt());
            integrationData.setCreatedAfter(testRun.getCreatedAt());
            integrationData.setEnv(testRun.getEnv());
            integrationData.setTestRunId(testRun.getId().toString());
            integrationData.setZafiraServiceUrl(urlResolver.buildWebURL());

            // ConfigXML parsing for TestRunName generation
            Configuration configuration = readArguments(testRun.getConfigXML());
            Map<String, String> configMap = parseConfigToMap(configuration);
            integrationData.setTestRunName(testRun.getName(configMap));

            // IntegrationType-specific properties adding
            switch (integrationTag) {
                case TESTRAIL_TESTCASE_UUID:
                    configuration.getArg().forEach(arg -> {
                        if (arg.getKey().contains("testrail_assignee")) {
                            integrationData.getCustomParams().put("assignee", arg.getValue());
                        } else if (arg.getKey().contains("testrail_milestone")) {
                            integrationData.getCustomParams().put("milestone", arg.getValue());
                        } else if (arg.getKey().contains("testrail_run_name")) {
                            integrationData.getCustomParams().put("testrail_run_name", arg.getValue());
                        }
                    });
                    break;
                case QTEST_TESTCASE_UUID:
                    configuration.getArg().forEach(arg -> {
                        if (arg.getKey().contains("qtest_cycle_name")) {
                            integrationData.getCustomParams().put("cycle_name", arg.getValue());
                        } else if (arg.getKey().contains("qtest_suite_name") && !StringUtils.isEmpty(arg.getValue())) {
                            integrationData.setTestRunName(arg.getValue());
                        }
                    });
            }
        }
        return integrationData;
    }

}