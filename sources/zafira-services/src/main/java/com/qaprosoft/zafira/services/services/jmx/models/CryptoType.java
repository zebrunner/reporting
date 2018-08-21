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
 ******************************************************************************/
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
