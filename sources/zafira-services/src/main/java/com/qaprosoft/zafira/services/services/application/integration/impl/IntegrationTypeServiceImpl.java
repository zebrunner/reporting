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
package com.qaprosoft.zafira.services.services.application.integration.impl;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.IntegrationTypeMapper;
import com.qaprosoft.zafira.models.db.integration.IntegrationType;
import com.qaprosoft.zafira.services.exceptions.EntityNotExistsException;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IntegrationTypeServiceImpl implements IntegrationTypeService {

    private static final String ERR_MSG_INTEGRATION_TYPE_NOT_FOUND = "No integration types found by id: %d";
    private static final String ERR_MSG_INTEGRATION_TYPE_NOT_FOUND_BY_NAME = "No integration types found by name: %s";
    private static final String ERR_MSG_INTEGRATION_TYPE_NOT_FOUND_BY_INTEGRATION_ID = "No integration types found by integration id: %d";

    private final IntegrationTypeMapper integrationTypeMapper;

    public IntegrationTypeServiceImpl(IntegrationTypeMapper integrationTypeMapper) {
        this.integrationTypeMapper = integrationTypeMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationType retrieveById(Long id) {
        IntegrationType integrationType = integrationTypeMapper.findById(id);
        if (integrationType == null) {
            throw new EntityNotExistsException(String.format(ERR_MSG_INTEGRATION_TYPE_NOT_FOUND, id));
        }
        return integrationType;
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationType retrieveByName(String name) {
        IntegrationType integrationType = integrationTypeMapper.findByName(name);
        if (integrationType == null) {
            throw new EntityNotExistsException(String.format(ERR_MSG_INTEGRATION_TYPE_NOT_FOUND_BY_NAME, name));
        }
        return integrationType;
    }

    @Override
    public IntegrationType retrieveByIntegrationId(Long integrationId) {
        IntegrationType integrationType = integrationTypeMapper.findByIntegrationId(integrationId);
        if (integrationType == null) {
            throw new EntityNotExistsException(String.format(ERR_MSG_INTEGRATION_TYPE_NOT_FOUND_BY_INTEGRATION_ID, integrationId));
        }
        return integrationType;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationType> retrieveAll() {
        return integrationTypeMapper.findAll();
    }

}