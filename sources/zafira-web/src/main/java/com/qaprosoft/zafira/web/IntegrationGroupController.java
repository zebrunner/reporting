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

import com.qaprosoft.zafira.models.dto.integration.IntegrationGroupDTO;
import com.qaprosoft.zafira.service.integration.IntegrationGroupService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Api("Integration groups API")
@CrossOrigin
@RequestMapping(path = "api/integration-groups", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class IntegrationGroupController extends AbstractController {

    private final IntegrationGroupService integrationGroupService;
    private final Mapper mapper;

    public IntegrationGroupController(IntegrationGroupService integrationGroupService, Mapper mapper) {
        this.integrationGroupService = integrationGroupService;
        this.mapper = mapper;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get integration groups", nickname = "getAll", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('VIEW_INTEGRATIONS')")
    @GetMapping()
    public List<IntegrationGroupDTO> getAll() {
        return integrationGroupService.retrieveAll().stream()
                                 .map(integration -> mapper.map(integration, IntegrationGroupDTO.class))
                                 .collect(Collectors.toList());
    }
}
