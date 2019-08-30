package com.qaprosoft.zafira.dbaccess.persistence;

import com.qaprosoft.zafira.models.entity.integration.IntegrationSetting;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;


public interface IntegrationSettingRepository extends Repository<IntegrationSetting, Long> {

    @EntityGraph(value = "integrationSetting.expanded")
    Optional<IntegrationSetting> findById(Long id);

    @EntityGraph(value = "integrationSetting.expanded")
    Optional<IntegrationSetting> findByIntegrationIdAndParamName(Long integrationId, String paramName);

    @EntityGraph(value = "integrationSetting.expanded")
    Optional<IntegrationSetting> findByIntegrationTypeNameAndParamName(String integrationTypeName, String paramName);

    @EntityGraph(value = "integrationSetting.expanded")
    List<IntegrationSetting> findAll();

    @EntityGraph(value = "integrationSetting.expanded")
    List<IntegrationSetting> findAllByEncryptedTrue();

    @EntityGraph(value = "integrationSetting.expanded")
    IntegrationSetting save(IntegrationSetting integrationSetting);

}
