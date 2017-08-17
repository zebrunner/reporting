package com.qaprosoft.zafira.services.util;

import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Created by irina on 16.8.17.
 */
public class ZafiraEncryptor implements DataEncryptor{

    private static final Logger LOGGER = Logger.getLogger(ZafiraEncryptor.class);
    private String algorithm;
    private Cipher cipher;
    private Key key;
    private String salt;
    private static final int ITERATIONS = 2;


    public ZafiraEncryptor (String cryptoAlgorithm, Key key, String salt) {

        this.algorithm = cryptoAlgorithm;
        this.key = key;
        this.salt = salt;
        try {
            this.cipher = Cipher.getInstance(this.algorithm);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException var5) {
            LOGGER.error(var5.getMessage(), var5);
        }

    }

    public String encrypt(String value) throws Exception {

        try {

            this.cipher.init(Cipher.ENCRYPT_MODE, this.key);
            String valueToEnc = null;
            String eValue = value;
            for (int i = 0; i < ITERATIONS; i++) {
                valueToEnc = salt + eValue;
                byte[] encValue = this.cipher.doFinal(valueToEnc.getBytes());
                eValue = new BASE64Encoder().encode(encValue);
            }
            return eValue;
        } catch (Exception var3) {
            throw new RuntimeException("Error while encrypting, check your key or salt! " + var3.getMessage(), var3);
        }
    }

    public String decrypt(String value) throws Exception {
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, this.key);
            String dValue = null;
            String valueToDecrypt = value;
            for (int i = 0; i < ITERATIONS; i++) {
                byte[] decodedValue = new BASE64Decoder().decodeBuffer(valueToDecrypt);
                byte[] decValue = this.cipher.doFinal(decodedValue);
                dValue = new String(decValue).substring(salt.length());
                valueToDecrypt = dValue;
            }
            return dValue;
        }
        catch (Exception var3) {
            throw new RuntimeException("Error while decrypting, check your key or salt! " + var3.getMessage(), var3);
        }
    }

}
