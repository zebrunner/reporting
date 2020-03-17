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
package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.integration.Integration;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IntegrationMapper {

    void create(@Param("integration") Integration integration, @Param("integrationTypeId") Long integrationTypeId);

    Integration findById(Long id);

    Integration findByBackReferenceId(String backReferenceId);

    Integration findDefaultByIntegrationTypeId(Long integrationTypeId);

    Integration findDefaultByIntegrationTypeName(String integrationTypeName);

    List<Integration> findAll();

    List<Integration> findByIntegrationTypeId(Long integrationTypeId);

    List<Integration> findByIntegrationGroupName(String integrationGroupName);

    void update(Integration integration);

}
