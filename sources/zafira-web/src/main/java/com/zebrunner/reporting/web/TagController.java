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
package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.TagIntegrationData;
import com.zebrunner.reporting.domain.dto.tag.IntegrationTag;
import com.zebrunner.reporting.domain.dto.tag.TagIntegrationDataDTO;
import com.zebrunner.reporting.service.TagService;
import com.zebrunner.reporting.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api("Tags operations")
@RequestMapping(path = "api/tags", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TagController extends AbstractController {

    private final TagService tagService;
    private final Mapper mapper;

    public TagController(TagService tagService, Mapper mapper) {
        this.tagService = tagService;
        this.mapper = mapper;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves integration information", nickname = "getTestIntegrationInfo", httpMethod = "GET", response = TagIntegrationDataDTO.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/{ciRunId}/integration")
    public TagIntegrationDataDTO exportTagIntegrationData(@PathVariable("ciRunId") String ciRunId,
                                                          @RequestParam("integrationTag") IntegrationTag tagName) {
        TagIntegrationData tagIntegrationData = tagService.exportTagIntegrationData(ciRunId, tagName);
        return mapper.map(tagIntegrationData, TagIntegrationDataDTO.class);
    }

}