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
package com.qaprosoft.zafira.dbaccess.dao.mysql.application;

import com.qaprosoft.zafira.models.db.integration.IntegrationSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface IntegrationSettingMapper {

    void create(@Param("integrationSettings") Set<IntegrationSetting> integrationSettings, @Param("integrationId") Long integrationId);

    IntegrationSetting findById(Long id);

    IntegrationSetting findByIntegrationIdAndParamName(@Param("integrationId") Long integrationId, @Param("paramName") String paramName);

    IntegrationSetting findByIntegrationTypeNameAndParamName(@Param("integrationTypeName") String integrationTypeName, @Param("paramName") String paramName);

    List<IntegrationSetting> findAll();

    void update(IntegrationSetting integrationSetting);

}
