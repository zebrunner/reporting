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
package com.qaprosoft.zafira.services.services.application.jmx.context;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

public class EmailContext extends AbstractContext
{

    private JavaMailSender javaMailSender;
    private String fromAddress;

    public EmailContext(String host, int port, String user, String fromAddress, String password)
    {
        this.javaMailSender = new JavaMailSenderImpl();

        final String authValue;
        if (!StringUtils.isBlank(user) && !StringUtils.isBlank(password)) {
            authValue = "true";
            ((JavaMailSenderImpl) this.javaMailSender).setUsername(user);
            ((JavaMailSenderImpl) this.javaMailSender).setPassword(password);
        } else {
            authValue = "false";
        }

        ((JavaMailSenderImpl) this.javaMailSender).setDefaultEncoding("UTF-8");
        ((JavaMailSenderImpl) this.javaMailSender).setJavaMailProperties(new Properties()
        {
            private static final long serialVersionUID = -7384945982042097581L;
            {
                setProperty("mail.smtp.auth", authValue);
                setProperty("mail.smtp.starttls.enable", "true");
            }
        });
        ((JavaMailSenderImpl) this.javaMailSender).setHost(host);
        ((JavaMailSenderImpl) this.javaMailSender).setPort(port);
        this.fromAddress = fromAddress;
    }

    public JavaMailSender getJavaMailSender()
    {
        return javaMailSender;
    }

    public void setJavaMailSender(JavaMailSender javaMailSender)
    {
        this.javaMailSender = javaMailSender;
    }

    public String getFromAddress()
    {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress)
    {
        this.fromAddress = fromAddress;
    }
}
