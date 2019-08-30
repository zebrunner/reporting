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
package com.qaprosoft.zafira.services.services.application.integration;


import com.qaprosoft.zafira.models.entity.integration.IntegrationSetting;

import java.util.List;
import java.util.Set;

public interface IntegrationSettingService {

//    /**
//     * Saves integration settings. All settings must to belong concrete integration type
//     * @param integrationSettings - integration settings to save
//     * @param integrationId - integration to reference
//     * @return created integration settings
//     */
//    Set<IntegrationSetting> create(List<IntegrationSetting> integrationSettings, Long integrationId);

    IntegrationSetting retrieveById(Long id);

    IntegrationSetting retrieveByIntegrationIdAndParamName(Long integrationId, String paramName);

    IntegrationSetting retrieveByIntegrationTypeNameAndParamName(String integrationTypeName, String paramName);

    List<IntegrationSetting> retrieveAllEncrypted();

    IntegrationSetting update(IntegrationSetting integrationSetting);

    /**
     * Updates integration settings. All settings must to belong concrete integration type
     * @param integrationSettings - integration settings to update
     * @param integrationId - integration to reference
     * @return updated integration settings
     */
    Set<IntegrationSetting> update(List<IntegrationSetting> integrationSettings, Long integrationId);

}
