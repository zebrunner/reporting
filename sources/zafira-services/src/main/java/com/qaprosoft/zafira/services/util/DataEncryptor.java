package com.qaprosoft.zafira.services.util;

/**
 * Created by irina on 17.8.17.
 */
public interface DataEncryptor {

    String encrypt (String value) throws Exception;

    String decrypt (String value) throws Exception;

}
