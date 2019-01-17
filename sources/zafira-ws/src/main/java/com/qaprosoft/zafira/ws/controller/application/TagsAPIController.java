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
package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.models.db.config.Configuration;
import com.qaprosoft.zafira.models.dto.tag.IntegrationTag;
import com.qaprosoft.zafira.models.dto.tag.IntegrationDataType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.TagService;
import com.qaprosoft.zafira.services.services.application.TestRunService;
import com.qaprosoft.zafira.services.util.URLResolver;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Controller
@Api(value = "Tags operations")
@RequestMapping("api/tags")
public class TagsAPIController extends AbstractController
{
	@Autowired
	private TagService tagService;

    @Autowired
    private TestRunService testRunService;

    @Autowired
    private URLResolver urlResolver;

    @ResponseStatusDetails
    @ApiOperation(value = "Get integration info", nickname = "getTestIntegrationInfo", code = 200, httpMethod = "GET", response = IntegrationDataType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(value = "{ciRunId}/integration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    IntegrationDataType getIntegrationInfo(@PathVariable(value = "ciRunId") String ciRunId, @RequestParam(value = "integrationTag") IntegrationTag integrationTag) throws ServiceException, JAXBException {
        IntegrationDataType integrationData = new IntegrationDataType();
        TestRun testRun = testRunService.getTestRunByCiRunIdFull(ciRunId);
        if (testRun != null){
            tagService.setTestInfoByIntegrationTag(ciRunId, integrationTag, integrationData);

            //finishedAt value generation based on startedAt & elapsed
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(testRun.getStartedAt());
            if(testRun.getElapsed() != null){
                calendar.add(Calendar.SECOND, testRun.getElapsed());
            }

            integrationData.setFinishedAt(calendar.getTime());
            integrationData.setStartedAt(testRun.getStartedAt());
            integrationData.setCreatedAfter(testRun.getCreatedAt());
            integrationData.setEnv(testRun.getEnv());
            integrationData.setTestRunId(testRun.getId().toString());
            integrationData.setZafiraServiceUrl(urlResolver.buildWebURL());

            //ConfigXML parsing for TestRunName generation
            Configuration configuration = testRunService.readConfiguration(testRun.getConfigXML());
            Map <String, String> configMap = new HashMap<>();
            for (Argument arg : configuration.getArg())
            {
                configMap.put(arg.getKey(), arg.getValue());
            }

            integrationData.setTestRunName(testRun.getName(configMap));

            //IntegrationType-specific properties adding
            switch (integrationTag){
                case TESTRAIL_TESTCASE_UUID:
                    configuration.getArg().forEach(arg -> {
                        if(arg.getKey().contains("testrail_assignee")){
                            integrationData.getCustomParams().put("assignee", arg.getValue());
                        } else if (arg.getKey().contains("testrail_milestone")){
                            integrationData.getCustomParams().put("milestone", arg.getValue());
                        } else if (arg.getKey().contains("testrail_run_name")){
                            integrationData.getCustomParams().put("testrail_run_name", arg.getValue());
                        }
                    });
                    break;
                case QTEST_TESTCASE_UUID:
                    configuration.getArg().forEach(arg -> {
                        if(arg.getKey().contains("qtest_cycle_name")){
                            integrationData.getCustomParams().put("cycle_name", arg.getValue());
                        } else if (arg.getKey().contains("qtest_suite_name") && !StringUtils.isEmpty(arg.getValue())){
                            integrationData.setTestRunName(arg.getValue());
                        }
                    });
            }
        }
        return integrationData;
    }
}