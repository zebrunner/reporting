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
package com.zebrunner.reporting.service.integration.impl;

import com.zebrunner.reporting.persistence.repository.IntegrationGroupRepository;
import com.zebrunner.reporting.domain.entity.integration.IntegrationGroup;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IntegrationGroupServiceImpl implements IntegrationGroupService {

    private static final String ERR_MSG_INTEGRATION_GROUP_NOT_FOUND_BY_NAME = "Integration group not found by name: %s";
    private static final String ERR_MSG_INTEGRATION_GROUP_NOT_FOUND_BY_INTEGRATION_TYPE = "Integration group not found by type id: %d";

    private final IntegrationGroupRepository integrationGroupRepository;

    public IntegrationGroupServiceImpl(IntegrationGroupRepository integrationGroupRepository) {
        this.integrationGroupRepository = integrationGroupRepository;
    }

    @Override
    public List<IntegrationGroup> retrieveAll() {
        return integrationGroupRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationGroup retrieveByIntegrationTypeId(Long integrationTypeId) {
        return integrationGroupRepository.findByTypeId(integrationTypeId)
                                         .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_GROUP_NOT_FUND, ERR_MSG_INTEGRATION_GROUP_NOT_FOUND_BY_INTEGRATION_TYPE, integrationTypeId));
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationGroup retrieveByName(String name) {
        return integrationGroupRepository.findByName(name)
                                         .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_GROUP_NOT_FUND, ERR_MSG_INTEGRATION_GROUP_NOT_FOUND_BY_NAME, name));
    }

}
