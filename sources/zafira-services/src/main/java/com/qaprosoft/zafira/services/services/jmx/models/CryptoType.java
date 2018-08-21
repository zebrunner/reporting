package com.qaprosoft.zafira.services.services.jmx.models;

import org.jasypt.util.text.BasicTextEncryptor;

public class CryptoType extends AbstractType
{

    private String type;
    private int size = 0;
    private String key;
    private String salt;
    private BasicTextEncryptor basicTextEncryptor;

    public CryptoType(String type, int size, String key, String salt)
    {
        this.type = type;
        this.size = size;
        this.key = key;
        this.salt = salt;
        this.basicTextEncryptor = new BasicTextEncryptor();
        this.basicTextEncryptor.setPassword(key + salt);
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
    }

    public String getSalt()
    {
        return salt;
    }

    public void setSalt(String salt)
    {
        this.salt = salt;
    }

    public BasicTextEncryptor getBasicTextEncryptor()
    {
        return basicTextEncryptor;
    }

    public void setBasicTextEncryptor(BasicTextEncryptor basicTextEncryptor)
    {
        this.basicTextEncryptor = basicTextEncryptor;
    }
}
