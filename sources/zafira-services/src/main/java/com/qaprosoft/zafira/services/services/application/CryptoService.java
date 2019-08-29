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
package com.qaprosoft.zafira.services.services.application;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.lang.StringUtils;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.EncryptorInitializationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CryptoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoService.class);

    private final SettingsService settingsService;
    private final String salt;
    private final Map<String, CryptoDriven<?>> cryptoDrivenServices;

    public CryptoService(SettingsService settingsService,
                         @Value("${crypto-salt}") String salt,
                         Map<String, CryptoDriven<?>> cryptoDrivenServices
    ) {
        this.settingsService = settingsService;
        this.salt = salt;
        this.cryptoDrivenServices = cryptoDrivenServices;
    }

    @PostConstruct
    public void init() {
        generateKeyIfNeed();
    }

    public String encrypt(String strToEncrypt) {
        BasicTextEncryptor basicTextEncryptor = getBasicTextEncryptor();
        return basicTextEncryptor.encrypt(strToEncrypt);
    }

    public String decrypt(String strToDecrypt) {
        BasicTextEncryptor basicTextEncryptor = getBasicTextEncryptor();
        return basicTextEncryptor.decrypt(strToDecrypt);
    }

    @Transactional(readOnly = true)
    public void generateKeyIfNeed() {
        String result = getCryptoKey();
        if (StringUtils.isBlank(result)) {
            regenerateKey();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void regenerateKey() {
        String key = null;
        String cryptoKeyType = getCryptoKeyType();
        int cryptoKeySize = getCryptoKeySize();
        try {
            key = Base64.getEncoder().encodeToString(generateKey(cryptoKeyType, cryptoKeySize).getEncoded());
        } catch (Exception e) {
            LOGGER.error("Unable to generate key: " + e.getMessage());
        }
        Setting keySetting = settingsService.getSettingByName("KEY");
        keySetting.setValue(key);
        settingsService.updateSetting(keySetting);
    }

    @Transactional(rollbackFor = Exception.class)
    public void reencrypt() {
        Map<String, Collection<?>> tempMap = new HashMap<>(collectCollectionsToReencrypt());
        tempMap.forEach(this::decryptCryptoDrivenServiceCollection);
        regenerateKey();
        tempMap.forEach((serviceName, collection) -> {
            encryptCryptoDrivenServiceCollection(serviceName, collection);
            CryptoDriven cryptoDrivenService = cryptoDrivenServices.get(serviceName);
            cryptoDrivenService.afterReencryptOperation(collection);
        });
    }

    private Map<String, Collection<?>> collectCollectionsToReencrypt() {
        return cryptoDrivenServices.entrySet().stream()
                                   .collect(Collectors.toMap(Map.Entry::getKey, cryptoDrivenEntry -> cryptoDrivenEntry.getValue().getEncryptedCollection()));
    }

    @SuppressWarnings("unchecked")
    private void decryptCryptoDrivenServiceCollection(String serviceName, Collection<?> collection) {
        CryptoDriven cryptoDrivenService = cryptoDrivenServices.get(serviceName);
        collection.forEach(o -> {
            String encryptedValue = cryptoDrivenService.getEncryptedValue(o);
            String decryptedValue = decrypt(encryptedValue);
            cryptoDrivenService.setEncryptedValue(o, decryptedValue);
        });
    }

    @SuppressWarnings("unchecked")
    private void encryptCryptoDrivenServiceCollection(String serviceName, Collection<?> collection) {
        CryptoDriven cryptoDrivenService = cryptoDrivenServices.get(serviceName);
        collection.forEach(o -> {
            String decryptedValue = cryptoDrivenService.getEncryptedValue(o);
            String encryptedValue = encrypt(decryptedValue);
            cryptoDrivenService.setEncryptedValue(o, encryptedValue);
        });
    }

    private static SecretKey generateKey(String keyType, int size) throws NoSuchAlgorithmException {
        LOGGER.debug("generating key use algorithm: '" + keyType + "'; size: " + size);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(keyType);
        keyGenerator.init(size);
        return keyGenerator.generateKey();
    }

    private BasicTextEncryptor getBasicTextEncryptor() {
        String key = getCryptoKey();
        BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
        try {
            if (!StringUtils.isEmpty(key)) {
                basicTextEncryptor.setPassword(key + salt);
            } else {
                throw new EncryptorInitializationException();
            }
        } catch (Exception e) {
            LOGGER.error("Unable to initialize Crypto Tool, salt or key might be null: " + e.getMessage(), e);
        }
        return basicTextEncryptor;
    }

    private String getCryptoKey() {
        Setting setting = settingsService.getSettingByName("KEY");
        return setting.getValue();
    }

    private String getCryptoKeyType() {
        Setting setting = settingsService.getSettingByName("CRYPTO_KEY_TYPE");
        return setting.getValue();
    }

    private int getCryptoKeySize() {
        Setting setting = settingsService.getSettingByName("CRYPTO_KEY_SIZE");
        return Integer.parseInt(setting.getValue());
    }

}
