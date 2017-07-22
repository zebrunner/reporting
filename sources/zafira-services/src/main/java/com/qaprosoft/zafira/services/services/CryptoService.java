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
import java.util.List;

/**
 * Created by irina on 21.7.17.
 */

@Service
public class CryptoService {

    private static final Logger LOGGER = Logger.getLogger(CryptoService.class);

    private CryptoTool cryptoTool;

    private String key;

    private String type = null;
    private String algorithm = null;
    private int size = 0;


    @Autowired
    private SettingsService settingsService;

    @PostConstruct
    public void getCryptoInfo() throws ServiceException {


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
            }
        }
        initCryptoTool(algorithm, type, size);
    }

    public void initCryptoTool (String algorithm, String type, String path, int size){
        try
        {
            if (!StringUtils.isEmpty(algorithm) && !StringUtils.isEmpty(type) && !StringUtils.isEmpty(path) && size!=0)
            {
                this.cryptoTool = new CryptoTool(algorithm,type,path);
                this.key = settingsService.getSettingByName("KEY").getValue();
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

    public String getKey()
    {
        return this.key;
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
