package com.qaprosoft.zafira.dbaccess.persistence;

import com.qaprosoft.zafira.models.entity.integration.IntegrationGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;


public interface IntegrationGroupRepository extends Repository<IntegrationGroup, Long> {

    @EntityGraph(value = "integrationGroup.expanded")
    Optional<IntegrationGroup> findById(Long id);

    @EntityGraph(value = "integrationGroup.expanded")
    Optional<IntegrationGroup> findByName(String name);

//    IntegrationGroup findIntegrationGroupBy(Long integrationTypeId);

    @EntityGraph(value = "integrationGroup.expanded")
    List<IntegrationGroup> findAll();

}
