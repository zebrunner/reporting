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
package com.qaprosoft.zafira.dbaccess.persistence;

import com.qaprosoft.zafira.models.entity.integration.IntegrationSetting;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface IntegrationSettingRepository extends CrudRepository<IntegrationSetting, Long> {

    @EntityGraph(value = "integrationSetting.expanded")
    Optional<IntegrationSetting> findById(Long id);

    @EntityGraph(value = "integrationSetting.expanded")
    Optional<IntegrationSetting> findByIntegrationIdAndParamName(Long integrationId, String paramName);

    @EntityGraph(value = "integrationSetting.expanded")
    Optional<IntegrationSetting> findByIntegrationTypeNameAndParamName(String integrationTypeName, String paramName);

    @EntityGraph(value = "integrationSetting.expanded")
    List<IntegrationSetting> findAll();

    @EntityGraph(value = "integrationSetting.expanded")
    @Query("select ist from IntegrationSetting ist join ist.integration i join i.type it where it.id = :integrationTypeId")
    List<IntegrationSetting> findAllByIntegrationTypeId(@Param("integrationTypeId") Long integrationTypeId);

    @EntityGraph(value = "integrationSetting.expanded")
    List<IntegrationSetting> findAllByEncryptedTrue();

    @EntityGraph(value = "integrationSetting.expanded")
    IntegrationSetting save(IntegrationSetting integrationSetting);

}
