package com.qaprosoft.zafira.dbaccess.persistence;

import com.qaprosoft.zafira.models.entity.integration.IntegrationGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;


public interface IntegrationGroupRepository extends Repository<IntegrationGroup, Long> {

    @EntityGraph(value = "integrationGroup.expanded")
    Optional<IntegrationGroup> findById(Long id);

    @EntityGraph(value = "integrationGroup.expanded")
    Optional<IntegrationGroup> findByName(String name);

    @EntityGraph(value = "integrationGroup.expanded")
    @Query(value = "Select * From zafira.integration_groups ig left join zafira.integration_types it On ig.id = it.integration_group_id Where it.id = :typeId", nativeQuery = true)
    Optional<IntegrationGroup> findByTypeId(Long typeId);

    @EntityGraph(value = "integrationGroup.expanded")
    List<IntegrationGroup> findAll();

}
