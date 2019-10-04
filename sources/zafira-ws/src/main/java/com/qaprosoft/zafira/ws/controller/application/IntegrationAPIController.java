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
package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import com.qaprosoft.zafira.models.dto.integration.IntegrationDTO;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.models.entity.integration.IntegrationInfo;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.impl.StorageProviderService;
import com.qaprosoft.zafira.services.services.application.integration.tool.impl.google.GoogleService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Api("Integrations API")
@CrossOrigin
@RequestMapping(path = "api/integrations", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class IntegrationAPIController extends AbstractController {

    private final IntegrationService integrationService;
    private final StorageProviderService storageProviderService;
    private final GoogleService googleService;
    private final Mapper mapper;

    public IntegrationAPIController(IntegrationService integrationService, StorageProviderService storageProviderService, GoogleService googleService, Mapper mapper) {
        this.integrationService = integrationService;
        this.storageProviderService = storageProviderService;
        this.googleService = googleService;
        this.mapper = mapper;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Create integration", nickname = "createIntegration", httpMethod = "POST", response = IntegrationDTO.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INTEGRATIONS')")
    @PostMapping()
    public IntegrationDTO create(@RequestBody @Valid IntegrationDTO integrationDTO, @RequestParam("integrationTypeId") Long integrationTypeId) {
        Integration integration = mapper.map(integrationDTO, Integration.class);
        integration = integrationService.create(integration, integrationTypeId);

        IntegrationDTO integrationUpdateResultDTO =  mapper.map(integration, IntegrationDTO.class);

        IntegrationInfo integrationInfo = integrationService.retrieveInfoByIntegration(integration);
        integrationUpdateResultDTO.setConnected(integrationInfo.isConnected());

        return integrationUpdateResultDTO;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get integrations", nickname = "getAll", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('VIEW_INTEGRATIONS')")
    @GetMapping()
    public List<IntegrationDTO> getAll(
            @RequestParam(name = "groupId", required = false) Long groupId,
            @RequestParam(name = "groupName", required = false) String groupName
    ) {
        List<Integration> integrations;
        if (groupId != null) {
            integrations = integrationService.retrieveIntegrationsByGroupId(groupId);
        } else if (groupName != null) {
            integrations = integrationService.retrieveIntegrationsByGroupName(groupName);
        } else {
            integrations = integrationService.retrieveAll();
        }
        return integrations.stream()
                           .map(integration -> mapper.map(integration, IntegrationDTO.class))
                           .collect(Collectors.toList());
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get amazon temporary credentials", nickname = "getAmazonTemporaryCredentials", httpMethod = "GET", response = SessionCredentials.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('VIEW_INTEGRATIONS')")
    @GetMapping("/creds/amazon")
    public SessionCredentials getAmazonTemporaryCredentials() {
        return storageProviderService.getTemporarySessionCredentials()
                                     .orElse(null);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get google temporary credentials", nickname = "getGoogleTemporaryCredentials", httpMethod = "GET", response = String.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('VIEW_INTEGRATIONS')")
    @GetMapping("/creds/google")
    public String getGoogleTemporaryCredentials() throws IOException {
        return googleService.getTemporaryAccessToken();
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update integration", nickname = "updateIntegration", httpMethod = "PUT", response = IntegrationDTO.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INTEGRATIONS')")
    @PutMapping("/{id}")
    public IntegrationDTO update(@RequestBody IntegrationDTO integrationDTO, @PathVariable("id") Long id) {
        Integration integration = mapper.map(integrationDTO, Integration.class);
        integration.setId(id);

        IntegrationDTO integrationUpdateResultDTO =  mapper.map(integrationService.update(integration), IntegrationDTO.class);

        IntegrationInfo integrationInfo = integrationService.retrieveInfoByIntegration(integration);
        integrationUpdateResultDTO.setConnected(integrationInfo.isConnected());

        return integrationUpdateResultDTO;
    }

}
