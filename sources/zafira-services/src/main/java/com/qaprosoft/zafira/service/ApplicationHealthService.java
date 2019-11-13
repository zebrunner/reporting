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
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.dbaccess.persistence.IntegrationRepository;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.service.exception.ExternalSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static com.qaprosoft.zafira.service.exception.ExternalSystemException.ExternalSystemErrorDetail.POSTGRES_VERSION_CAN_NOT_BE_FOUND;

@Service
public class ApplicationHealthService {

    private static final String ERR_MSG_POSTGRES_VERSION_NOT_FOUND = "Unable to retrieve Postgres version";

    private final SettingsService settingsService;
    private final IntegrationRepository integrationRepository;

    public ApplicationHealthService(SettingsService settingsService, IntegrationRepository integrationRepository) {
        this.settingsService = settingsService;
        this.integrationRepository = integrationRepository;
    }

    @Transactional(readOnly = true)
    public String getStatus() {
        String version = settingsService.getPostgresVersion();
        if (StringUtils.isEmpty(version)) {
            throw new ExternalSystemException(POSTGRES_VERSION_CAN_NOT_BE_FOUND, ERR_MSG_POSTGRES_VERSION_NOT_FOUND);
        }
        Integration firstIntegration = integrationRepository.findById(1L)
                                                            .orElse(null);
        return String.format("Service is up and running. Integration: %s", firstIntegration);
    }
}
