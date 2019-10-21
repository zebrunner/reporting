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
package com.qaprosoft.zafira.service.integration.impl;

import com.qaprosoft.zafira.dbaccess.persistence.IntegrationTypeRepository;
import com.qaprosoft.zafira.models.entity.integration.IntegrationType;
import com.qaprosoft.zafira.service.exception.ResourceNotFoundException;
import com.qaprosoft.zafira.service.integration.IntegrationTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_TYPE_NOT_FOUND;

@Service
public class IntegrationTypeServiceImpl implements IntegrationTypeService {

    private static final String ERR_MSG_INTEGRATION_TYPE_NOT_FOUND = "No integration types found by id: %d";
    private static final String ERR_MSG_INTEGRATION_TYPE_NOT_FOUND_BY_NAME = "No integration types found by name: %s";
    private static final String ERR_MSG_INTEGRATION_TYPE_NOT_FOUND_BY_INTEGRATION_ID = "No integration types found by integration id: %d";

    private final IntegrationTypeRepository integrationTypeRepository;

    public IntegrationTypeServiceImpl(IntegrationTypeRepository integrationTypeRepository) {
        this.integrationTypeRepository = integrationTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationType retrieveById(Long id) {
        return integrationTypeRepository.findById(id)
                                        .orElseThrow(() -> new ResourceNotFoundException(INTEGRATION_TYPE_NOT_FOUND, ERR_MSG_INTEGRATION_TYPE_NOT_FOUND, id));
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationType retrieveByName(String name) {
        return integrationTypeRepository.findByName(name)
                                        .orElseThrow(() -> new ResourceNotFoundException(INTEGRATION_TYPE_NOT_FOUND, ERR_MSG_INTEGRATION_TYPE_NOT_FOUND_BY_NAME, name));
    }

    @Override
    public IntegrationType retrieveByIntegrationId(Long integrationId) {
        return integrationTypeRepository.findByIntegrationId(integrationId)
                                        .orElseThrow(() -> new ResourceNotFoundException(INTEGRATION_TYPE_NOT_FOUND, ERR_MSG_INTEGRATION_TYPE_NOT_FOUND_BY_INTEGRATION_ID, integrationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationType> retrieveAll() {
        return integrationTypeRepository.findAll();
    }

}
