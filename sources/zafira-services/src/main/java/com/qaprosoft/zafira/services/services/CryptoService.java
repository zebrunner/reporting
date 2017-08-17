package com.qaprosoft.zafira.services.services;

import com.qaprosoft.carina.core.foundation.crypto.CryptoTool;
import com.qaprosoft.carina.core.foundation.crypto.SecretKeyManager;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.util.SaltManager;
import com.qaprosoft.zafira.services.util.ZafiraEncryptor;
import net.rcarz.jiraclient.JiraClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by irina on 21.7.17.
 */

@ManagedResource(objectName="bean:name=cryptoService", description="Crypto init Managed Bean",
        currencyTimeLimit=15, persistPolicy="OnUpdate", persistPeriod=200)
public class CryptoService {

    private static final Logger LOGGER = Logger.getLogger(CryptoService.class);

    private ZafiraEncryptor zafiraEncryptor;
    private String type = null;
    private String algorithm = null;
    private int size = 0;
    private String key = null;
    private String salt;

    @Autowired
    private SettingsService settingsService;

    @PostConstruct
    public void initZafiraEncryptor() throws Exception {

        List<Setting> cryptoSettings = settingsService.getSettingsByTool("CRYPTO");

        for (Setting setting : cryptoSettings){

            switch(setting.getName()){

                case "CRYPTO_KEY_TYPE":
                    type = setting.getValue();
                    break;
                case "CRYPTO_ALGORITHM":
                    algorithm = setting.getValue();
                    break;
                case "CRYPTO_KEY_SIZE":
                    size = Integer.valueOf(setting.getValue());
                    break;
                case "KEY":
                    String dbKey = setting.getValue();
                    if (dbKey == null){
                        generateKey();
                        key = settingsService.getSettingByName("KEY").getValue();
                    }
                    else {
                        key = dbKey;
                    }
                    break;
            }
        }
         init(algorithm, type);
    }


    @ManagedOperation(description="Change Crypto initialization")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "algorithm", description = "Crypto algorithm"),
            @ManagedOperationParameter(name = "type", description = "Crypto type")})
    private void init(String algorithm, String type){
        try
        {
            if (!StringUtils.isEmpty(algorithm) && !StringUtils.isEmpty(type))
            {
                SecretKey secretKey = SecretKeyManager.getKey(key,type);
                zafiraEncryptor = new ZafiraEncryptor(algorithm, secretKey, salt);
            }
        } catch (Exception e)
        {
            LOGGER.error("Unable to initialize Crypto Tool: " + e.getMessage(), e);
        }
    }

    public String encrypt(String strToEncrypt) throws Exception {
        return zafiraEncryptor.encrypt(strToEncrypt);
    }

    public String decrypt (String strToDecrypt) throws Exception {
        return zafiraEncryptor.decrypt(strToDecrypt);
    }

    @Transactional(rollbackFor = Exception.class)
    public void generateKey() throws Exception {
        Setting dbKey = settingsService.getSettingByName("KEY");
        String key = null;
        try {
            key = new String(Base64.encodeBase64(SecretKeyManager.generateKey(type, size).getEncoded()));
        } catch (Exception e) {
            LOGGER.error("Unable to generate key: " + e.getMessage());
        }
        Setting keySetting = settingsService.getSettingByName("KEY");
        keySetting.setValue(key);
        settingsService.updateSetting(keySetting);
        settingsService.reEncrypt(key, dbKey);
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @ManagedAttribute(description="Get cryptoKey")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
