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

import com.qaprosoft.zafira.models.entity.integration.IntegrationType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;


public interface IntegrationTypeRepository extends Repository<IntegrationType, Long> {

    @EntityGraph(value = "integrationType.expanded")
    Optional<IntegrationType> findById(Long id);

    @EntityGraph(value = "integrationType.expanded")
    Optional<IntegrationType> findByName(String name);

    @EntityGraph(value = "integrationType.expanded")
    @Query(value = "Select * From zafira.integration_types it left join zafira.integrations i On it.id = i.integration_type_id Where i.id = :integrationId", nativeQuery = true)
    Optional<IntegrationType> findByIntegrationId(Long integrationId);

    @EntityGraph(value = "integrationType.expanded")
    List<IntegrationType> findAll();

}
