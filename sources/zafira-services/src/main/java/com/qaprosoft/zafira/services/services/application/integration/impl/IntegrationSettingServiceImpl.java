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
package com.qaprosoft.zafira.services.services.application.integration.impl;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.IntegrationSettingMapper;
import com.qaprosoft.zafira.models.db.integration.IntegrationParam;
import com.qaprosoft.zafira.models.db.integration.IntegrationSetting;
import com.qaprosoft.zafira.models.db.integration.IntegrationType;
import com.qaprosoft.zafira.services.exceptions.EntityNotExistsException;
import com.qaprosoft.zafira.services.exceptions.IntegrationException;
import com.qaprosoft.zafira.services.services.application.CryptoService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationParamService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationSettingService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IntegrationSettingServiceImpl implements IntegrationSettingService {

    private static final String ERR_MSG_INTEGRATION_SETTING_NOT_FOUND = "Integration setting with id '%d' not found";
    private static final String ERR_MSG_INTEGRATION_SETTING_NOT_FOUND_BY_INTEGRATION_ID_AND_PARAM_NAME = "Integration setting with integration id '%d' and parameter name '%s' not found";
    private static final String ERR_MSG_INTEGRATION_SETTING_NOT_FOUND_BY_INTEGRATION_TYPE_NAME_AND_PARAM_NAME = "Integration setting with integration type name '%s' and parameter name '%s' not found";
    private static final String ERR_MSG_DUPLICATE_INTEGRATION_SETTINGS = "Duplicate settings for integration with id %d were found: %s";
    private static final String ERR_MSG_INTEGRATION_SETTINGS_PARAMS_LENGTH = "Integration settings %s are required";
    private static final String ERR_MSG_INTEGRATION_SETTINGS_PARAMS_OWNS = "All settings should to belong to one integration";
    private static final String ERR_MSG_EMPTY_MANDATORY_INTEGRATION_SETTINGS = "Empty mandatory settings for integration with id %d were found: %s";

    private final IntegrationSettingMapper integrationSettingMapper;
    private final IntegrationTypeService integrationTypeService;
    private final IntegrationParamService integrationParamService;
    private final CryptoService cryptoService;

    public IntegrationSettingServiceImpl(IntegrationSettingMapper integrationSettingMapper,
                                         IntegrationTypeService integrationTypeService,
                                         IntegrationParamService integrationParamService,
                                         CryptoService cryptoService
    ) {
        this.integrationSettingMapper = integrationSettingMapper;
        this.integrationTypeService = integrationTypeService;
        this.integrationParamService = integrationParamService;
        this.cryptoService = cryptoService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Set<IntegrationSetting> create(List<IntegrationSetting> integrationSettings, Long integrationId) {
        Set<IntegrationSetting> integrationSettingSet = new HashSet<>(integrationSettings);
        validateSettings(integrationSettingSet, integrationSettings, integrationId);
        integrationSettings.forEach(integrationSetting -> {
            IntegrationParam integrationParam = integrationParamService.retrieveById(integrationSetting.getIntegrationParam().getId());
            integrationSetting.setIntegrationParam(integrationParam);
            encryptIfNeed(integrationSetting);
        });
        integrationSettingMapper.create(integrationSettingSet, integrationId);
        return integrationSettingSet;
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationSetting retrieveById(Long id) {
        IntegrationSetting integrationSetting = integrationSettingMapper.findById(id);
        if (integrationSetting == null) {
            throw new EntityNotExistsException(String.format(ERR_MSG_INTEGRATION_SETTING_NOT_FOUND, id));
        }
        return integrationSetting;
    }

    @Override
    public IntegrationSetting retrieveByIntegrationIdAndParamName(Long integrationId, String paramName) {
        IntegrationSetting integrationSetting = integrationSettingMapper.findByIntegrationIdAndParamName(integrationId, paramName);
        if (integrationSetting == null) {
            throw new EntityNotExistsException(String.format(ERR_MSG_INTEGRATION_SETTING_NOT_FOUND_BY_INTEGRATION_ID_AND_PARAM_NAME, integrationId, paramName));
        }
        return integrationSetting;
    }

    @Override
    public IntegrationSetting retrieveByIntegrationTypeNameAndParamName(String integrationTypeName, String paramName) {
        IntegrationSetting integrationSetting = integrationSettingMapper.findByIntegrationTypeNameAndParamName(integrationTypeName, paramName);
        if (integrationSetting == null) {
            throw new EntityNotExistsException(String.format(ERR_MSG_INTEGRATION_SETTING_NOT_FOUND_BY_INTEGRATION_TYPE_NAME_AND_PARAM_NAME, integrationTypeName, paramName));
        }
        return integrationSetting;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IntegrationSetting update(IntegrationSetting integrationSetting) {
        integrationSettingMapper.update(integrationSetting);
        return integrationSetting;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Set<IntegrationSetting> update(List<IntegrationSetting> integrationSettings, Long integrationId) {
        Set<IntegrationSetting> integrationSettingSet = new HashSet<>(integrationSettings);
        validateSettings(integrationSettingSet, integrationSettings, integrationId);
        integrationSettingSet.forEach(this::update);
        return integrationSettingSet;
    }

    /**
     * Encrypts value on create or update
     * @param integrationSetting - setting to encrypt
     */
    private void encryptIfNeed(IntegrationSetting integrationSetting) {
        try {
            IntegrationSetting dbIntegrationSetting = retrieveById(integrationSetting.getId());
            integrationSetting.setEncrypted(dbIntegrationSetting.isEncrypted());
            if (integrationSetting.getValue() == null || integrationSetting.getValue().isBlank()) {
                integrationSetting.setEncrypted(false);
            } else if (dbIntegrationSetting.getIntegrationParam().isNeedEncryption() && !dbIntegrationSetting.getValue().equals(integrationSetting.getValue())) {
                integrationSetting.setEncrypted(true);
                String encryptedValue = cryptoService.encrypt(integrationSetting.getValue());
                integrationSetting.setValue(encryptedValue);
            }
        } catch (EntityNotExistsException e) {
            integrationSetting.setEncrypted(false);
            IntegrationParam integrationParam = integrationParamService.retrieveById(integrationSetting.getIntegrationParam().getId());
            if (integrationSetting.getValue() == null || integrationSetting.getValue().isBlank()) {
                integrationSetting.setEncrypted(false);
            } else if (integrationParam.isNeedEncryption()) {
                integrationSetting.setEncrypted(true);
                String encryptedValue = cryptoService.encrypt(integrationSetting.getValue());
                integrationSetting.setValue(encryptedValue);
            }
        }
    }

    private void validateSettings(Set<IntegrationSetting> integrationSettingSet, List<IntegrationSetting> integrationSettings, Long integrationId) {
        // check duplicates
        if (integrationSettings.size() != integrationSettingSet.size()) {
            Set<IntegrationSetting> duplicateSettings = recognizeDuplicateIntegrationSettings(integrationSettingSet, integrationSettings);
            String duplicates = buildSettingsNameString(duplicateSettings);
            throw new IntegrationException(String.format(ERR_MSG_DUPLICATE_INTEGRATION_SETTINGS, integrationId, duplicates));
        }
        // check owns - all parameters for one type and all exist
        List<IntegrationParam> integrationParams = integrationSettingSet.stream()
                                                                        .map(IntegrationSetting::getIntegrationParam)
                                                                        .collect(Collectors.toList());
        IntegrationType integrationType = integrationTypeService.retrieveByIntegrationId(integrationId);
        if (integrationType.getIntegrationParams().size() != integrationParams.size()) {
            String requiredParameters = integrationType.getIntegrationParams().stream()
                                                       .map(IntegrationParam::getName)
                                                       .collect(Collectors.joining(", "));
            throw new IntegrationException(String.format(ERR_MSG_INTEGRATION_SETTINGS_PARAMS_LENGTH, requiredParameters));
        }
        if (!integrationType.getIntegrationParams().containsAll(integrationParams)) {
            throw new IntegrationException(ERR_MSG_INTEGRATION_SETTINGS_PARAMS_OWNS);
        }
        // check mandatories
        Set<IntegrationSetting> emptyMandatorySettings = recognizeEmptyMandatoryIntegrationSettings(integrationSettingSet);
        if (!emptyMandatorySettings.isEmpty()) {
            String emptyMandatories = buildSettingsNameString(emptyMandatorySettings);
            throw new IntegrationException(String.format(ERR_MSG_EMPTY_MANDATORY_INTEGRATION_SETTINGS, integrationId, emptyMandatories));
        }
    }

    private String buildSettingsNameString(Collection<IntegrationSetting> integrationSettings) {
        return integrationSettings.stream()
                                  .map(integrationSetting -> integrationSetting.getIntegrationParam().getName())
                                  .collect(Collectors.joining(", "));
    }

    private Set<IntegrationSetting> recognizeDuplicateIntegrationSettings(Set<IntegrationSetting> integrationSettingSet, List<IntegrationSetting> integrationSettings) {
        return integrationSettingSet.stream()
                                    .filter(integrationSetting -> integrationSettings.indexOf(integrationSetting) != integrationSettings.lastIndexOf(integrationSetting))
                                    .collect(Collectors.toSet());
    }

    private Set<IntegrationSetting> recognizeEmptyMandatoryIntegrationSettings(Set<IntegrationSetting> integrationSettingSet) {
        return integrationSettingSet.stream()
                                    .filter(integrationSetting -> {
                                        boolean isValueEmpty = integrationSetting.getValue() == null && integrationSetting.getBinaryData().length == 0;
                                        return integrationSetting.getIntegrationParam().isMandatory() && isValueEmpty;
                                    })
                                    .collect(Collectors.toSet());
    }

}
