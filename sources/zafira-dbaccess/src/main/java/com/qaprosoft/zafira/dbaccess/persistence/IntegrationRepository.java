package com.qaprosoft.zafira.dbaccess.persistence;

import com.qaprosoft.zafira.models.entity.integration.Integration;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface IntegrationRepository extends Repository<Integration, Long> {

    Integration save(Integration integration);

    @EntityGraph(value = "integration.expanded")
    Optional<Integration> findById(Long name);

    @EntityGraph(value = "integration.expanded")
    List<Integration> findAll();

    @EntityGraph(value = "integration.expanded")
    List<Integration> getIntegrationsByTypeId(Long typeId);

    @EntityGraph(value = "integration.expanded")
    @Query("select i from Integration i join fetch i.settings s join fetch s.param join i.type t join t.group g where g.id = :groupId")
    List<Integration> findByGroupId(@Param("groupId") Long groupId);

    @EntityGraph(value = "integration.expanded")
    Optional<Integration> findIntegrationByBackReferenceId(String backReferenceId);

    @EntityGraph(value = "integration.expanded")
    @Query(value = "Select * From integrations i left join integration_types it On i.integration_type_id = it.id left join integration_groups ig on it.integration_group_id = ig.id Where ig.name = :typeGroupName", nativeQuery = true)
    List<Integration> findIntegrationByTypeGroupName(String typeGroupName);

    @EntityGraph(value = "integration.expanded")
    @Query("Select i From Integration i Where i.type.id = :integrationTypeId and i.isDefault = true")
    Optional<Integration> findIntegrationByTypeIdAndDefaultIsTrue(Long integrationTypeId);

    @EntityGraph(value = "integration.expanded")
    @Query("Select i From Integration i Where i.type.name = :integrationTypeName and i.isDefault = true")
    Optional<Integration> findIntegrationByTypeNameAndDefaultIsTrue(String integrationTypeName);

    @EntityGraph(value = "integration.expanded")
    List<Integration> findIntegrationsByTypeName(String integrationTypeName);
}
