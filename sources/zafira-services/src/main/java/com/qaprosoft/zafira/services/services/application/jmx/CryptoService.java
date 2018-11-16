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
package com.qaprosoft.zafira.services.services.application.jmx;

import static com.qaprosoft.zafira.models.db.Setting.SettingType.KEY;
import static com.qaprosoft.zafira.models.db.Setting.Tool.CRYPTO;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.EncryptorInitializationException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.jmx.context.CryptoContext;

/**
 * Created by irina on 21.7.17.
 */
@ManagedResource(objectName = "bean:name=cryptoService", description = "Crypto init Managed Bean", currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200)
public class CryptoService implements IJMXService<CryptoContext> {
    private static final Logger LOGGER = Logger.getLogger(CryptoService.class);

    private String salt;

    @Autowired
    private SettingsService settingsService;

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

            putContext(CRYPTO, new CryptoContext(type, size, key, this.salt));

            String dbKey = settingsService.getSettingByType(KEY).getValue();
            if (StringUtils.isBlank(dbKey)) {
                generateKey();
                key = settingsService.getSettingByType(KEY).getValue();
            } else {
                key = dbKey;
            }

            initCryptoTool(key);

        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    @ManagedOperation(description = "Change Crypto initialization")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "key", description = "Crypto key") })
    public void initCryptoTool(String key) {
        try {
            if (!StringUtils.isEmpty(key)) {
                getCryptoType().setKey(key);
            } else {
                throw new EncryptorInitializationException();
            }
        } catch (Exception e) {
            LOGGER.error("Unable to initialize Crypto Tool, salt or key might be null: " + e.getMessage(), e);
        }
    }

    public String encrypt(String strToEncrypt) throws Exception {
        return getCryptoType().getBasicTextEncryptor().encrypt(strToEncrypt);
    }

    public String decrypt(String strToDecrypt) throws Exception {
        return getCryptoType().getBasicTextEncryptor().decrypt(strToDecrypt);
    }

    public void generateKey() throws Exception {
        String key = null;
        try {
            key = new String(Base64
                    .encodeBase64(generateKey(getCryptoType().getType(), getCryptoType().getSize()).getEncoded()));
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

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public boolean isConnected() {
        boolean connected = false;
        try {
            connected = getCryptoType().getKey() != null && salt != null;
        } catch (Exception e) {
            LOGGER.error("Unable to connect to JIRA", e);
        }
        return connected;
    }

    private static SecretKey generateKey(String keyType, int size) throws NoSuchAlgorithmException {
        LOGGER.debug("generating key use algorithm: '" + keyType + "'; size: " + size);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(keyType);
        keyGenerator.init(size);
        return keyGenerator.generateKey();
    }

    @ManagedAttribute(description = "Get current crypto entity")
    public CryptoContext getCryptoType() {
        return getContext(CRYPTO);
    }
}
