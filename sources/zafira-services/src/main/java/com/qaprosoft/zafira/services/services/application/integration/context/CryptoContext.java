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
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.integration.context;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.IntegrationException;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.text.BasicTextEncryptor;

import java.util.Map;

import static com.qaprosoft.zafira.services.services.application.integration.context.CryptoContext.CryptoAdditionalProperty.SALT;

public class CryptoContext extends AbstractContext
{

    private String type;
    private int size;
    private String key;
    private String salt;
    private String cryptoAlgorithm;
    private BasicTextEncryptor basicTextEncryptor;

    public CryptoContext(Map<Setting.SettingType, String> settings, Map<CryptoAdditionalProperty, String> additionalProperties)
    {
        super(settings, true);

        String type = settings.get(Setting.SettingType.CRYPTO_KEY_TYPE);
        int size = Integer.valueOf(settings.get(Setting.SettingType.CRYPTO_KEY_SIZE));
        String key = settings.get(Setting.SettingType.KEY);
        String salt = additionalProperties.get(SALT);
        this.cryptoAlgorithm = settings.get(Setting.SettingType.CRYPTO_ALGORITHM);
        this.type = type;
        this.size = size;
        this.key = key;
        this.salt = salt;
        if(!StringUtils.isBlank(key) && !StringUtils.isBlank(salt)) {
            initEncryptor();
        }
    }

    public enum CryptoAdditionalProperty implements AdditionalProperty {
        SALT
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
        initEncryptor();
    }

    public String getSalt()
    {
        return salt;
    }

    public void setSalt(String salt)
    {
        this.salt = salt;
        initEncryptor();
    }

    public String getCryptoAlgorithm() {
        return cryptoAlgorithm;
    }

    public void setCryptoAlgorithm(String cryptoAlgorithm) {
        this.cryptoAlgorithm = cryptoAlgorithm;
    }

    public BasicTextEncryptor getBasicTextEncryptor()
    {
        return basicTextEncryptor;
    }

    public void setBasicTextEncryptor(BasicTextEncryptor basicTextEncryptor)
    {
        this.basicTextEncryptor = basicTextEncryptor;
    }

    private void initEncryptor() {
        if(StringUtils.isBlank(key) || StringUtils.isBlank(salt)) {
            throw new IntegrationException("Crypto key and salt must not be empty");
        }
        basicTextEncryptor = new BasicTextEncryptor();
        basicTextEncryptor.setPassword(key + salt);
    }
}
