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

import com.qaprosoft.zafira.dbaccess.persistence.IntegrationSettingRepository;
import com.qaprosoft.zafira.models.entity.integration.IntegrationParam;
import com.qaprosoft.zafira.models.entity.integration.IntegrationSetting;
import com.qaprosoft.zafira.models.entity.integration.IntegrationType;
import com.qaprosoft.zafira.services.exceptions.IntegrationException;
import com.qaprosoft.zafira.services.exceptions.ResourceNotFoundException;
import com.qaprosoft.zafira.services.services.application.CryptoDriven;
import com.qaprosoft.zafira.services.services.application.CryptoService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationParamService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationSettingService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.qaprosoft.zafira.services.exceptions.ResourceNotFoundException.ResourceNotFoundErrorDetail.INTEGRATION_SETTING_NOT_FOUND;

@Service
public class IntegrationSettingServiceImpl implements IntegrationSettingService, CryptoDriven<IntegrationSetting> {

    private static final String ERR_MSG_INTEGRATION_SETTING_NOT_FOUND = "Integration setting with id '%d' not found";
    private static final String ERR_MSG_INTEGRATION_SETTING_NOT_FOUND_BY_INTEGRATION_ID_AND_PARAM_NAME = "Integration setting with integration id '%d' and parameter name '%s' not found";
    private static final String ERR_MSG_INTEGRATION_SETTING_NOT_FOUND_BY_INTEGRATION_TYPE_NAME_AND_PARAM_NAME = "Integration setting with integration type name '%s' and parameter name '%s' not found";
    private static final String ERR_MSG_DUPLICATE_INTEGRATION_SETTINGS = "Duplicate settings for integration type with id %d were found: %s";
    private static final String ERR_MSG_INTEGRATION_SETTINGS_PARAMS_LENGTH = "Integration settings %s are required";
    private static final String ERR_MSG_INTEGRATION_SETTINGS_PARAMS_OWNS = "All settings should to belong to one integration";
    private static final String ERR_MSG_EMPTY_MANDATORY_INTEGRATION_SETTINGS = "Empty mandatory settings for integration type with id %d were found: %s";

    private final IntegrationSettingRepository integrationSettingRepository;
    private final IntegrationTypeService integrationTypeService;
    private final IntegrationParamService integrationParamService;
    private final CryptoService cryptoService;

    public IntegrationSettingServiceImpl(IntegrationSettingRepository integrationSettingRepository,
                                         IntegrationTypeService integrationTypeService,
                                         IntegrationParamService integrationParamService,
                                         CryptoService cryptoService
    ) {
        this.integrationSettingRepository = integrationSettingRepository;
        this.integrationTypeService = integrationTypeService;
        this.integrationParamService = integrationParamService;
        this.cryptoService = cryptoService;
    }

    @Override
    @Transactional()
    public List<IntegrationSetting> batchCreate(List<IntegrationSetting> integrationSettings, Long typeId) {
        validateSettings(integrationSettings, typeId);
        batchEncrypt(integrationSettings);
        Iterable<IntegrationSetting> settingIterable = integrationSettingRepository.saveAll(integrationSettings);
        integrationSettings = new ArrayList<>();
        settingIterable.forEach(integrationSettings::add);
        return integrationSettings;
    }

    @Override
    @Transactional()
    public List<IntegrationSetting> batchUpdate(List<IntegrationSetting> integrationSettings, Long typeId) {
        validateSettings(integrationSettings, typeId);
        batchEncrypt(integrationSettings);
        Iterable<IntegrationSetting> settingIterable = integrationSettingRepository.saveAll(integrationSettings);
        integrationSettings = new ArrayList<>();
        settingIterable.forEach(integrationSettings::add);
        return integrationSettings;
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationSetting retrieveById(Long id) {
        return integrationSettingRepository.findById(id)
                                           .orElseThrow(() -> new ResourceNotFoundException(INTEGRATION_SETTING_NOT_FOUND, ERR_MSG_INTEGRATION_SETTING_NOT_FOUND, id));
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationSetting retrieveByIntegrationIdAndParamName(Long integrationId, String paramName) {
        return integrationSettingRepository.findByIntegrationIdAndParamName(integrationId, paramName)
                                           .orElseThrow(() -> new ResourceNotFoundException(INTEGRATION_SETTING_NOT_FOUND, ERR_MSG_INTEGRATION_SETTING_NOT_FOUND_BY_INTEGRATION_ID_AND_PARAM_NAME, integrationId, paramName));
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationSetting retrieveByIntegrationTypeNameAndParamName(String integrationTypeName, String paramName) {
        return integrationSettingRepository.findByIntegrationTypeNameAndParamName(integrationTypeName, paramName)
                                           .orElseThrow(() -> new ResourceNotFoundException(INTEGRATION_SETTING_NOT_FOUND, ERR_MSG_INTEGRATION_SETTING_NOT_FOUND_BY_INTEGRATION_TYPE_NAME_AND_PARAM_NAME, integrationTypeName, paramName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationSetting> retrieveAllEncrypted() {
        return integrationSettingRepository.findAllByEncryptedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationSetting> retrieveByIntegrationTypeId(Long integrationTypeId) {
        return integrationSettingRepository.findAllByIntegrationTypeId(integrationTypeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IntegrationSetting update(IntegrationSetting integrationSetting) {
        integrationSettingRepository.save(integrationSetting);
        return integrationSetting;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<IntegrationSetting> getEncryptedCollection() {
        return retrieveAllEncrypted();
    }

    @Override
    public void afterReencryptOperation(Collection<IntegrationSetting> reencryptedCollection) {
        reencryptedCollection.forEach(this::update);
    }

    @Override
    public String getEncryptedValue(IntegrationSetting entity) {
        return entity.getValue();
    }

    @Override
    public void setEncryptedValue(IntegrationSetting integrationSetting, String encryptedString) {
        integrationSetting.setValue(encryptedString);
    }

    private void batchEncrypt(List<IntegrationSetting> integrationSettings) {
        integrationSettings.forEach(this::encryptIfNeed);
    }

    /**
     * Encrypts value on create or update
     * @param integrationSetting - setting to encrypt
     */
    private void encryptIfNeed(IntegrationSetting integrationSetting) {
        try {
            if (integrationSetting.getId() == null) {
                IntegrationParam integrationParam = integrationParamService.retrieveById(integrationSetting.getParam().getId());
                if (integrationParam.isNeedEncryption()) {
                    integrationSetting.setEncrypted(true);
                    String encryptedValue = cryptoService.encrypt(integrationSetting.getValue());
                    integrationSetting.setValue(encryptedValue);
                }
            } else {
                IntegrationSetting dbIntegrationSetting = retrieveById(integrationSetting.getId());
                if (integrationSetting.getValue() == null || integrationSetting.getValue().isBlank()) {
                    integrationSetting.setEncrypted(false);
                } else if (dbIntegrationSetting.getParam().isNeedEncryption() && !dbIntegrationSetting.getValue().equals(integrationSetting.getValue())) {
                    integrationSetting.setEncrypted(true);
                    String encryptedValue = cryptoService.encrypt(integrationSetting.getValue());
                    integrationSetting.setValue(encryptedValue);
                } else {
                    integrationSetting.setEncrypted(dbIntegrationSetting.isEncrypted());
                }
            }
        } catch (ResourceNotFoundException e) {
            integrationSetting.setEncrypted(false);
            IntegrationParam integrationParam = integrationParamService.retrieveById(integrationSetting.getParam().getId());
            if (integrationSetting.getValue() == null || integrationSetting.getValue().isBlank()) {
                integrationSetting.setEncrypted(false);
            } else if (integrationParam.isNeedEncryption()) {
                integrationSetting.setEncrypted(true);
                String encryptedValue = cryptoService.encrypt(integrationSetting.getValue());
                integrationSetting.setValue(encryptedValue);
            }
        }
    }

    private void validateSettings(List<IntegrationSetting> integrationSettings, Long integrationTypeId) {
        // check duplicates
        Set<IntegrationSetting> uniqueIntegrationSettings = new HashSet<>(integrationSettings);
        if (integrationSettings.size() != uniqueIntegrationSettings.size()) {
            Set<IntegrationSetting> duplicateSettings = recognizeDuplicateIntegrationSettings(uniqueIntegrationSettings, integrationSettings);
            String duplicates = buildSettingsNameString(duplicateSettings);
            throw new IntegrationException(String.format(ERR_MSG_DUPLICATE_INTEGRATION_SETTINGS, integrationTypeId, duplicates));
        }
        // check owns - all parameters for one type and all exist
        List<IntegrationParam> integrationParams = uniqueIntegrationSettings.stream()
                                                                        .map(IntegrationSetting::getParam)
                                                                        .collect(Collectors.toList());


        IntegrationType integrationType = integrationTypeService.retrieveById(integrationTypeId);
        if (integrationType.getParams().size() != integrationParams.size()) {
            String requiredParameters = integrationType.getParams().stream()
                                                       .map(IntegrationParam::getName)
                                                       .collect(Collectors.joining(", "));
            throw new IntegrationException(String.format(ERR_MSG_INTEGRATION_SETTINGS_PARAMS_LENGTH, requiredParameters));
        }
        if (!integrationType.getParams().containsAll(integrationParams)) {
            throw new IntegrationException(ERR_MSG_INTEGRATION_SETTINGS_PARAMS_OWNS);
        }
        // check mandatories
        Set<IntegrationSetting> emptyMandatorySettings = recognizeEmptyMandatoryIntegrationSettings(uniqueIntegrationSettings);
        if (!emptyMandatorySettings.isEmpty()) {
            String emptyMandatories = buildSettingsNameString(emptyMandatorySettings);
            throw new IntegrationException(String.format(ERR_MSG_EMPTY_MANDATORY_INTEGRATION_SETTINGS, integrationTypeId, emptyMandatories));
        }
    }

    private String buildSettingsNameString(Collection<IntegrationSetting> integrationSettings) {
        return integrationSettings.stream()
                                  .map(integrationSetting -> integrationSetting.getParam().getName())
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
                                        return integrationSetting.getParam().isMandatory() && isValueEmpty;
                                    })
                                    .collect(Collectors.toSet());
    }

}
