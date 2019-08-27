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

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.IntegrationGroupMapper;
import com.qaprosoft.zafira.models.db.integration.IntegrationGroup;
import com.qaprosoft.zafira.services.exceptions.EntityNotExistsException;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IntegrationGroupServiceImpl implements IntegrationGroupService {

    private static final String ERR_MSG_INTEGRATION_GROUP_NOT_FOUND_BY_NAME = "Integration group not found by name: %s";
    private static final String ERR_MSG_INTEGRATION_GROUP_NOT_FOUND_BY_INTEGRATION_TYPE = "Integration group not found by type id: %d";

    private final IntegrationGroupMapper integrationGroupMapper;

    public IntegrationGroupServiceImpl(IntegrationGroupMapper integrationGroupMapper) {
        this.integrationGroupMapper = integrationGroupMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationGroup retrieveByIntegrationTypeId(Long integrationTypeId) {
        IntegrationGroup integrationGroup = integrationGroupMapper.findByIntegrationTypeId(integrationTypeId);
        if (integrationGroup == null) {
            throw new EntityNotExistsException(String.format(ERR_MSG_INTEGRATION_GROUP_NOT_FOUND_BY_INTEGRATION_TYPE, integrationTypeId));
        }
        return integrationGroup;
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationGroup retrieveByName(String name) {
        IntegrationGroup integrationGroup = integrationGroupMapper.findByName(name);
        if (integrationGroup == null) {
            throw new EntityNotExistsException(String.format(ERR_MSG_INTEGRATION_GROUP_NOT_FOUND_BY_NAME, name));
        }
        return integrationGroup;
    }
}
