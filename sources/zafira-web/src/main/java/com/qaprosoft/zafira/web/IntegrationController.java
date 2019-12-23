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

import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import com.qaprosoft.zafira.models.dto.integration.IntegrationDTO;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.models.entity.integration.IntegrationInfo;
import com.qaprosoft.zafira.service.integration.IntegrationService;
import com.qaprosoft.zafira.service.integration.tool.impl.StorageProviderService;
import com.qaprosoft.zafira.service.integration.tool.impl.google.GoogleService;
import com.qaprosoft.zafira.web.documented.IntegrationDocumentedController;
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

@CrossOrigin
@RequestMapping(path = "api/integrations", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class IntegrationController extends AbstractController implements IntegrationDocumentedController {

    private final IntegrationService integrationService;
    private final StorageProviderService storageProviderService;
    private final GoogleService googleService;
    private final Mapper mapper;

    public IntegrationController(IntegrationService integrationService, StorageProviderService storageProviderService, GoogleService googleService, Mapper mapper) {
        this.integrationService = integrationService;
        this.storageProviderService = storageProviderService;
        this.googleService = googleService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INTEGRATIONS')")
    @PostMapping()
    @Override
    public IntegrationDTO create(@RequestBody @Valid IntegrationDTO integrationDTO, @RequestParam("integrationTypeId") Long integrationTypeId) {
        Integration integration = mapper.map(integrationDTO, Integration.class);
        integration = integrationService.create(integration, integrationTypeId);

        IntegrationDTO integrationUpdateResultDTO =  mapper.map(integration, IntegrationDTO.class);

        IntegrationInfo integrationInfo = integrationService.retrieveInfoByIntegration(integration);
        integrationUpdateResultDTO.setConnected(integrationInfo.isConnected());

        return integrationUpdateResultDTO;
    }

    @PreAuthorize("hasPermission('VIEW_INTEGRATIONS') or isAuthenticated()")
    @GetMapping()
    @Override
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

    @PreAuthorize("hasPermission('VIEW_INTEGRATIONS')")
    @GetMapping("/creds/amazon")
    @Override
    public SessionCredentials getAmazonTemporaryCredentials() {
        return storageProviderService.getTemporarySessionCredentials()
                                     .orElse(null);
    }

    @PreAuthorize("hasPermission('VIEW_INTEGRATIONS')")
    @GetMapping("/creds/google")
    public String getGoogleTemporaryCredentials() throws IOException {
        return googleService.getTemporaryAccessToken();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INTEGRATIONS')")
    @PutMapping("/{id}")
    @Override
    public IntegrationDTO update(@RequestBody IntegrationDTO integrationDTO, @PathVariable("id") Long id) {
        Integration integration = mapper.map(integrationDTO, Integration.class);
        integration.setId(id);

        integration = integrationService.update(integration);
        IntegrationDTO integrationUpdateResultDTO =  mapper.map(integration, IntegrationDTO.class);

        IntegrationInfo integrationInfo = integrationService.retrieveInfoByIntegration(integration);
        integrationUpdateResultDTO.setConnected(integrationInfo.isConnected());

        return integrationUpdateResultDTO;
    }

}
