/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
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

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.qaprosoft.zafira.services.exceptions.IntegrationException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.EncryptorInitializationException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.context.CryptoContext;
import org.springframework.stereotype.Component;

@Component
public class CryptoService extends AbstractIntegration<CryptoContext> {

    private static final Logger LOGGER = Logger.getLogger(CryptoService.class);

    private final SettingsService settingsService;
    private final String salt;

    public CryptoService(SettingsService settingsService,
                         @Value("${zafira.crypto_salt}") String salt) {
        super(CRYPTO);
        this.settingsService = settingsService;
        this.salt = salt;
    }

    @Override
    public void init() {

        String type = null;
        int size = 0;
        String key = null;

        List<Setting> cryptoSettings;
        try {
            cryptoSettings = settingsService.getSettingsByTool(CRYPTO);

            for (Setting setting : cryptoSettings) {

                switch (Setting.SettingType.valueOf(setting.getName())) {

                case CRYPTO_KEY_TYPE:
                    type = setting.getValue();
                    break;
                case CRYPTO_KEY_SIZE:
                    size = Integer.valueOf(setting.getValue());
                    break;
                default:
                    break;
                }
            }

            putContext(new CryptoContext(type, size, key, this.salt));

            key = getKey().orElseThrow(() ->
                    new IntegrationException("Create an integration context before key generating"));
            initCryptoTool(key);

        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private void initCryptoTool(String key) {
        try {
            if (! StringUtils.isEmpty(key)) {
                context().setKey(key);
            } else {
                throw new EncryptorInitializationException();
            }
        } catch (Exception e) {
            LOGGER.error("Unable to initialize Crypto Tool, salt or key might be null: " + e.getMessage(), e);
        }
    }

    public String encrypt(String strToEncrypt) throws Exception {
        return context().getBasicTextEncryptor().encrypt(strToEncrypt);
    }

    public String decrypt(String strToDecrypt) throws Exception {
        return context().getBasicTextEncryptor().decrypt(strToDecrypt);
    }

    public Optional<String> getKey() throws Exception {
        return mapContext(context -> {
            String result = settingsService.getSettingByType(KEY).getValue();
            if (StringUtils.isBlank(result)) {
                generateKey();
                result = settingsService.getSettingByType(KEY).getValue();
            }
            return result;
        });
    }

    public void generateKey() throws ServiceException {
        String key = null;
        try {
            if(! mapContext(CryptoContext::getType).isPresent()) {
                init();
                return;
            }
            key = new String(Base64
                    .encodeBase64(generateKey(context().getType(), context().getSize()).getEncoded()));
        } catch (Exception e) {
            LOGGER.error("Unable to generate key: " + e.getMessage());
        }
        Setting keySetting = settingsService.getSettingByType(KEY);
        keySetting.setValue(key);
        settingsService.updateSetting(keySetting);
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
