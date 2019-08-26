package com.qaprosoft.zafira.dbaccess.persistence;

import com.qaprosoft.zafira.models.entity.integration.Integration;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

//@Repository
public interface IntegrationRepository extends CrudRepository<Integration, Long> {

    @EntityGraph(value = "integration.expanded")
    Optional<Integration> readById(Long name);
}
