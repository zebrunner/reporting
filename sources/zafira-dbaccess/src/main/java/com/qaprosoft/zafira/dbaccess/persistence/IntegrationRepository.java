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

import com.qaprosoft.zafira.models.entity.integration.Integration;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface IntegrationRepository extends JpaRepository<Integration, Long> {

    @EntityGraph(value = "integration.expanded")
    Optional<Integration> findById(Long name);

    @EntityGraph(value = "integration.expanded")
    List<Integration> findAll();

    @EntityGraph(value = "integration.expanded")
    List<Integration> getIntegrationsByTypeId(Long typeId);

    @EntityGraph(value = "integration.expanded")
    @Query("select i from Integration i join fetch i.settings s join fetch s.param join fetch i.type t join t.group g where g.id = :groupId")
    List<Integration> findByGroupId(@Param("groupId") Long groupId);

    @EntityGraph(value = "integration.expanded")
    Optional<Integration> findIntegrationByBackReferenceId(String backReferenceId);

    @EntityGraph(value = "integration.expanded")
    @Query("select i from Integration i join fetch i.settings s join fetch s.param join fetch i.type t join t.group g where g.name = :groupName")
    List<Integration> findIntegrationsByGroupName(@Param("groupName") String groupName);

    @EntityGraph(value = "integration.expanded")
    @Query("Select i From Integration i Where i.type.id = :integrationTypeId and i.isDefault = true")
    Optional<Integration> findIntegrationByTypeIdAndDefaultIsTrue(@Param("integrationTypeId") Long integrationTypeId);

    @EntityGraph(value = "integration.expanded")
    @Query("Select i From Integration i Where i.type.name = :integrationTypeName and i.isDefault = true")
    Optional<Integration> findIntegrationByTypeNameAndDefaultIsTrue(@Param("integrationTypeName") String integrationTypeName);

    @EntityGraph(value = "integration.expanded")
    List<Integration> findIntegrationsByTypeName(String integrationTypeName);

}
