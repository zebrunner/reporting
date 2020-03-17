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

import com.zebrunner.reporting.persistence.repository.IntegrationParamRepository;
import com.zebrunner.reporting.domain.entity.integration.IntegrationParam;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.integration.IntegrationParamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IntegrationParamServiceImpl implements IntegrationParamService {

    private static final String ERR_MSG_INTEGRATION_PARAM_NOT_FOUND = "Integration param with id '%d' not found";

    private final IntegrationParamRepository integrationParamRepository;

    public IntegrationParamServiceImpl(IntegrationParamRepository integrationParamRepository) {
        this.integrationParamRepository = integrationParamRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationParam retrieveById(Long id) {
        return integrationParamRepository.findById(id)
                                         .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_PARAM_NOT_FOUND, ERR_MSG_INTEGRATION_PARAM_NOT_FOUND, id));
    }

}
