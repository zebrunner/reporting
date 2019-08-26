package com.qaprosoft.zafira.dbaccess.persistence;

import com.qaprosoft.zafira.models.entity.integration.Integration;
import org.springframework.data.repository.CrudRepository;

//@Repository
public interface IntegrationRepository extends CrudRepository<Integration, Long> {
}
