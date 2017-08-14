package com.qaprosoft.zafira.services.services;

import com.qaprosoft.carina.core.foundation.crypto.CryptoTool;
import com.qaprosoft.carina.core.foundation.crypto.SecretKeyManager;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.List;

/**
 * Created by irina on 21.7.17.
 */

@Service
public class CryptoService {

    private static final Logger LOGGER = Logger.getLogger(CryptoService.class);

    private CryptoTool cryptoTool;

    private String type = null;
    private String algorithm = null;
    private int size = 0;
    private String key = null;

    @Autowired
    private SettingsService settingsService;

    @PostConstruct
    public void initCryptoTool() throws ServiceException {

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

            init(algorithm, type);
        }

    }

    private void init(String algorithm, String type){
        try
        {
            if (!StringUtils.isEmpty(algorithm) && !StringUtils.isEmpty(type))
            {
                SecretKey secretKey = SecretKeyManager.getKey(key,type);
                cryptoTool = new CryptoTool(algorithm, type, secretKey);
            }
        } catch (Exception e)
        {
            LOGGER.error("Unable to initialize Crypto Tool: " + e.getMessage(), e);
        }
    }

    public String encrypt(String strToEncrypt)
    {
         return cryptoTool.encrypt(strToEncrypt);
    }

    public String decrypt (String strToDecrypt)
    {
        return cryptoTool.decrypt(strToDecrypt);
    }

    public void generateKey() throws ServiceException {
        String key = null;
        try {
            key = new String(Base64.encodeBase64(SecretKeyManager.generateKey(type, size).getEncoded()));
        } catch (Exception e) {
            LOGGER.error("Unable to generate key: " + e.getMessage());
        }
        Setting keySetting = settingsService.getSettingByName("KEY");
        keySetting.setValue(key);
        settingsService.updateSetting(keySetting);
    }

    public boolean isAvailable(){
        return key != null;
    }
}
