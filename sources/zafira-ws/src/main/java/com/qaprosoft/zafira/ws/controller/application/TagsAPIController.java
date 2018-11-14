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

import com.qaprosoft.zafira.models.dto.IntegrationInfoType;
import com.qaprosoft.zafira.services.services.application.TagService;
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

import java.util.*;

@Controller
@Api(value = "Tags operations")
@RequestMapping("api/tags")
public class TagsAPIController extends AbstractController
{
	@Autowired
	private TagService tagService;

    @ResponseStatusDetails
	@ApiOperation(value = "Get integration info by tag name", nickname = "getIntegrationByTag", code = 200, httpMethod = "GET", response = IntegrationInfoType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{ciRunId}/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody IntegrationInfoType getIntegrationByTag(@PathVariable(value = "ciRunId") String ciRunId, @RequestParam(value = "name") String name) {
        List<String> integrationTagValues = tagService.getTagsByNameAndTestRunCiRunId(name, ciRunId);
        IntegrationInfoType integrationInfo = new IntegrationInfoType();
        List<String> testCaseList = new ArrayList<>();
        integrationTagValues.forEach (
                tagValue -> {
                    String[] tagInfoArray = tagValue.split("-");
                    if(integrationInfo.getProjectId() == null){
                        integrationInfo.setProjectId(tagInfoArray[0]);
                        integrationInfo.setSuiteId(tagInfoArray[1]);
                    }
                    testCaseList.add(tagValue.split("-")[2]);
                }
        );
        integrationInfo.setTestCaseIds(testCaseList);
		return integrationInfo;
	}
}
