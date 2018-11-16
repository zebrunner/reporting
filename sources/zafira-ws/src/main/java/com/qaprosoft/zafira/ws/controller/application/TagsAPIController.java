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
import com.qaprosoft.zafira.models.dto.tag.TestRailIntegrationType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.TagService;
import com.qaprosoft.zafira.services.services.application.TestRunService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBException;

@Controller
@Api(value = "Tags operations")
@RequestMapping("api/tags")
public class TagsAPIController extends AbstractController
{
	@Autowired
	private TagService tagService;

    @Autowired
    private TestRunService testRunService;

    @ResponseStatusDetails
    @ApiOperation(value = "Get TestRail integration info", nickname = "getTestRailIntegrationInfo", code = 200, httpMethod = "GET", response = TestRailIntegrationType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(value = "{ciRunId}/testrail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    TestRailIntegrationType getTestRailIntegrationInfo(@PathVariable(value = "ciRunId") String ciRunId, @RequestParam(value = "tagName") String tagName) throws ServiceException, JAXBException {
        TestRailIntegrationType testRailIntegration = new TestRailIntegrationType();
        TestRun testRun = testRunService.getTestRunByCiRunIdFull(ciRunId);
        IntegrationTag integrationTag = tagService.getIntegrationTagInfo(tagName, ciRunId);
        testRailIntegration.setProjectId(integrationTag.getProjectId());
        testRailIntegration.setSuiteId(integrationTag.getSuiteId());
        testRailIntegration.setTestCases(integrationTag.getTestCaseIds());
        testRailIntegration.setCreatedAfter(testRun.getCreatedAt().getTime());
        Configuration configuration = testRunService.readConfiguration(testRun.getConfigXML());
        for (Argument arg : configuration.getArg()) {
            if(arg.getKey().contains("testrail_assignee")){
                testRailIntegration.setCreatedBy(arg.getValue());
            } else if (arg.getKey().contains("testrail_milestone")){
                testRailIntegration.setMilestone(arg.getValue());
            }
        }
        testRailIntegration.setCreatedBy(testRun.getName(configuration));
        return testRailIntegration;
    }
}
