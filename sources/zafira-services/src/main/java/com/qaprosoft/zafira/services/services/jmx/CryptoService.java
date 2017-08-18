package com.qaprosoft.zafira.services.services.jmx;

import com.qaprosoft.carina.core.foundation.crypto.SecretKeyManager;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.EncryptorInitializationException;
import com.qaprosoft.zafira.services.services.SettingsService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.List;

import static com.qaprosoft.zafira.models.db.tools.Tool.CRYPTO;
import static com.qaprosoft.zafira.services.services.SettingsService.SettingType.*;

/**
 * Created by irina on 21.7.17.
 */

@ManagedResource(objectName="bean:name=cryptoService", description="Crypto init Managed Bean",
        currencyTimeLimit=15, persistPolicy="OnUpdate", persistPeriod=200)
public class CryptoService implements IJMXService {

    private static final Logger LOGGER = Logger.getLogger(CryptoService.class);

    private String type = null;
    private int size = 0;
    private String key = null;
    private String salt;
    private BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();


    @Autowired
    private SettingsService settingsService;

    @PostConstruct
    public void initZafiraEncryptor() throws Exception {

        List<Setting> cryptoSettings = settingsService.getSettingsByTool(CRYPTO.name());

        for (Setting setting : cryptoSettings){

            switch(SettingsService.SettingType.valueOf(setting.getName())){

                case CRYPTO_KEY_TYPE:
                    type = setting.getValue();
                    break;
                case CRYPTO_KEY_SIZE:
                    size = Integer.valueOf(setting.getValue());
                    break;
                case KEY:
                    String dbKey = setting.getValue();
                    if (dbKey == null){
                        generateKey();
                        key = settingsService.getSettingByName("KEY").getValue();
                    }
                    else {
                        key = dbKey;
                    }
                    init();
            }
        }
    }


    @ManagedOperation(description="Change Crypto initialization")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "key", description = "Crypto key")})
    public void init(){
        try
        {
            if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(salt) )
            {
                basicTextEncryptor.setPassword(key + salt);
            }
            else {
                throw new EncryptorInitializationException();
            }
        } catch (Exception e)
        {
            LOGGER.error("Unable to initialize Crypto Tool, salt or key might be null: " + e.getMessage(), e);
        }
    }

    public String encrypt(String strToEncrypt) throws Exception {
        return basicTextEncryptor.encrypt(strToEncrypt);
    }

    public String decrypt (String strToDecrypt) throws Exception {
        return basicTextEncryptor.decrypt(strToDecrypt);
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

    @Override
    public boolean isConnected() {
        boolean connected = false;
        try
        {
            connected = getKey() != null && salt != null;
        }
        catch(Exception e)
        {
            LOGGER.error("Unable to connect to JIRA", e);
        }
        return connected;
    }
}
