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

import com.qaprosoft.zafira.models.entity.integration.IntegrationGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IntegrationGroupRepository extends Repository<IntegrationGroup, Long> {

    @EntityGraph(value = "integrationGroup.expanded")
    Optional<IntegrationGroup> findById(Long id);

    @EntityGraph(value = "integrationGroup.expanded")
    Optional<IntegrationGroup> findByName(String name);

    @EntityGraph(value = "integrationGroup.expanded")
    @Query("select ig from IntegrationGroup ig join fetch ig.types it where it.id = :typeId")
    Optional<IntegrationGroup> findByTypeId(@Param("typeId") Long typeId);

    @EntityGraph(value = "integrationGroup.expanded")
    List<IntegrationGroup> findAll();

}
