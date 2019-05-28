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

import static com.qaprosoft.zafira.models.db.Setting.SettingType.KEY;
import static com.qaprosoft.zafira.models.db.Setting.Tool.CRYPTO;
import static com.qaprosoft.zafira.services.services.application.integration.context.CryptoContext.CryptoAdditionalProperty.SALT;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.qaprosoft.zafira.services.exceptions.IntegrationException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.EncryptorInitializationException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.context.CryptoContext;
import org.springframework.stereotype.Component;

@Component
public class CryptoService extends AbstractIntegration<CryptoContext> {

    private final SettingsService settingsService;
    private final String salt;

    public CryptoService(SettingsService settingsService,
            @Value("${zafira.crypto_salt}") String salt) {
        super(settingsService, CRYPTO, CryptoContext.class);
        this.settingsService = settingsService;
        this.salt = salt;
    }

    @Override
    public void init() {
        super.init();
        String key = getKey().orElseThrow(() -> new IntegrationException("Create an integration context before key generating"));
        if (context().getBasicTextEncryptor() == null || !key.equals(context().getKey())) {
            initCryptoTool(key);
        }
    }

    @Override
    public Map<CryptoContext.CryptoAdditionalProperty, String> additionalContextProperties() {
        Map<CryptoContext.CryptoAdditionalProperty, String> additionalProperties = new HashMap<>();
        additionalProperties.put(SALT, salt);
        return additionalProperties;
    }

    private void initCryptoTool(String key) {
        try {
            if (!StringUtils.isEmpty(key)) {
                context().setKey(key);
            } else {
                throw new EncryptorInitializationException();
            }
        } catch (Exception e) {
            LOGGER.error("Unable to initialize Crypto Tool, salt or key might be null: " + e.getMessage(), e);
        }
    }

    public String encrypt(String strToEncrypt) {
        return context().getBasicTextEncryptor().encrypt(strToEncrypt);
    }

    public String decrypt(String strToDecrypt) {
        return context().getBasicTextEncryptor().decrypt(strToDecrypt);
    }

    public Optional<String> getKey() {
        return mapContext(context -> {
            String result = settingsService.getSettingByType(KEY).getValue();
            if (StringUtils.isBlank(result)) {
                result = generateKey();
            }
            return result;
        });
    }

    public String generateKey() throws ServiceException {
        String key = null;
        try {
            if (!mapContext(CryptoContext::getType).isPresent()) {
                init();
                return null;
            }
            key = new String(Base64
                    .encodeBase64(generateKey(context().getType(), context().getSize()).getEncoded()));
        } catch (Exception e) {
            LOGGER.error("Unable to generate key: " + e.getMessage());
        }
        Setting keySetting = settingsService.getSettingByType(KEY);
        keySetting.setValue(key);
        settingsService.updateSetting(keySetting);
        return key;
    }

    public void regenerateKey() {
        String key = generateKey();
        context().setKey(key);
    }

    public String getSalt() {
        return salt;
    }

    @Override
    public boolean isConnected() {
        try {
            return context().getKey() != null && salt != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static SecretKey generateKey(String keyType, int size) throws NoSuchAlgorithmException {
        LOGGER.debug("generating key use algorithm: '" + keyType + "'; size: " + size);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(keyType);
        keyGenerator.init(size);
        return keyGenerator.generateKey();
    }

}
