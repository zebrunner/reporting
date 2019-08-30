package com.qaprosoft.zafira.dbaccess.persistence;

import com.qaprosoft.zafira.models.entity.integration.Integration;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

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
    Optional<Integration> findIntegrationByBackReferenceId(String backReferenceId);

    @EntityGraph(value = "integration.expanded")
    List<Integration> findIntegrationByTypeGroupName(String typeGroupName);

    @EntityGraph(value = "integration.expanded")
    @Query("Select i From Integration i Where i.type.id = :integrationTypeId and i.isDefault = true")
    Optional<Integration> findIntegrationByTypeIdAndDefaultIsTrue(Long integrationTypeId);

    @EntityGraph(value = "integration.expanded")
    @Query("Select i From Integration i Where i.type.name = :integrationTypeName and i.isDefault = true")
    Optional<Integration> findIntegrationByTypeNameAndDefaultIsTrue(String integrationTypeName);
}
