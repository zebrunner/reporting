package com.qaprosoft.zafira.dbaccess.persistence;

import com.qaprosoft.zafira.models.entity.integration.Integration;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface IntegrationRepository extends Repository<Integration, Long> {

    @EntityGraph(value = "integration.expanded")
    Optional<Integration> findById(Long name);

    @EntityGraph(value = "integration.expanded")
    List<Integration> findAll();
}
