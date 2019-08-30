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
