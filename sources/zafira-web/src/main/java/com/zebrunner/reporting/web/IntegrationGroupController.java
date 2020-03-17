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

import com.zebrunner.reporting.domain.dto.integration.IntegrationGroupDTO;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.web.documented.IntegrationGroupDocumentedController;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RequestMapping(path = "api/integration-groups", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class IntegrationGroupController extends AbstractController implements IntegrationGroupDocumentedController {

    private final IntegrationGroupService integrationGroupService;
    private final Mapper mapper;

    public IntegrationGroupController(IntegrationGroupService integrationGroupService, Mapper mapper) {
        this.integrationGroupService = integrationGroupService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasPermission('VIEW_INTEGRATIONS')")
    @GetMapping()
    @Override
    public List<IntegrationGroupDTO> getAll() {
        return integrationGroupService.retrieveAll().stream()
                                 .map(integration -> mapper.map(integration, IntegrationGroupDTO.class))
                                 .collect(Collectors.toList());
    }
}
