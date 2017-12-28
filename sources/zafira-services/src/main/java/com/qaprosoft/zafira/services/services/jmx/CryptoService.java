/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.services.services.jmx;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.EncryptorInitializationException;
import com.qaprosoft.zafira.services.services.SettingsService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.*;

import javax.annotation.PostConstruct;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.qaprosoft.zafira.models.db.Setting.Tool.CRYPTO;
import static com.qaprosoft.zafira.models.db.Setting.SettingType.*;

/**
 * Created by irina on 21.7.17.
 */
@ManagedResource(objectName = "bean:name=cryptoService", description = "Crypto init Managed Bean", currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200, persistLocation = "foo", persistName = "bar")
public class CryptoService implements IJMXService
{
	private static final Logger LOGGER = Logger.getLogger(CryptoService.class);

	private String type;
	private int size = 0;
	private String key;
	private String salt;
	private BasicTextEncryptor basicTextEncryptor;

	@Autowired
	private SettingsService settingsService;

	@Override
	@PostConstruct
	public void init()
	{

		List<Setting> cryptoSettings = null;
		this.basicTextEncryptor = new BasicTextEncryptor();
		try
		{
			cryptoSettings = settingsService.getSettingsByTool(CRYPTO);

			for (Setting setting : cryptoSettings)
			{

				switch (Setting.SettingType.valueOf(setting.getName()))
				{

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

			initCryptoTool();

		}
		catch (Exception e)
		{
			LOGGER.error(e);
		}
	}

	@ManagedOperation(description = "Change Crypto initialization")
	public void initCryptoTool()
	{
		try
		{
			String dbKey = settingsService.getSettingByType(KEY).getValue();
			if (StringUtils.isEmpty(dbKey))
			{
				generateKey();
				key = settingsService.getSettingByType(KEY).getValue();
			}
			else
			{
				key = dbKey;
			}
			if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(salt))
			{
				basicTextEncryptor.setPassword(key + salt);
			}
			else
			{
				throw new EncryptorInitializationException();
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to initialize Crypto Tool, salt or key might be null: " + e.getMessage(), e);
		}
	}

	public String encrypt(String strToEncrypt) throws Exception
	{
		return basicTextEncryptor.encrypt(strToEncrypt);
	}

	public String decrypt(String strToDecrypt) throws Exception
	{
		return basicTextEncryptor.decrypt(strToDecrypt);
	}

	public void generateKey() throws Exception
	{
		String key = null;
		try
		{
			key = new String(Base64.encodeBase64(generateKey(type, size).getEncoded()));
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to generate key: " + e.getMessage());
		}
		Setting keySetting = settingsService.getSettingByType(KEY);
		keySetting.setValue(key);
		settingsService.updateSetting(keySetting);
	}

	public String getSalt()
	{
		return salt;
	}

	public void setSalt(String salt)
	{
		this.salt = salt;
	}

	@ManagedAttribute(description = "Get cryptoKey")
	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	@Override
	public boolean isConnected()
	{
		boolean connected = false;
		try
		{
			connected = getKey() != null && salt != null;
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to connect to JIRA", e);
		}
		return connected;
	}

	public static SecretKey generateKey(String keyType, int size) throws NoSuchAlgorithmException
	{
		LOGGER.debug("generating key use algorithm: '" + keyType + "'; size: " + size);
		KeyGenerator keyGenerator = KeyGenerator.getInstance(keyType);
		keyGenerator.init(size);
		return keyGenerator.generateKey();
	}
}
