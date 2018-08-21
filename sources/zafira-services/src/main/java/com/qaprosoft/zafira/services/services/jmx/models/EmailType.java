package com.qaprosoft.zafira.services.services.jmx.models;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public class EmailType extends AbstractType
{

    private JavaMailSender javaMailSender;
    private String fromAddress;

    public EmailType(String host, int port, String user, String fromAddress, String password)
    {
        this.javaMailSender = new JavaMailSenderImpl();
        ((JavaMailSenderImpl) this.javaMailSender).setDefaultEncoding("UTF-8");
        ((JavaMailSenderImpl) this.javaMailSender).setJavaMailProperties(new Properties()
        {
            private static final long serialVersionUID = -7384945982042097581L;
            {
                setProperty("mail.smtp.auth", "true");
                setProperty("mail.smtp.starttls.enable", "true");
            }
        });
        ((JavaMailSenderImpl) this.javaMailSender).setHost(host);
        ((JavaMailSenderImpl) this.javaMailSender).setPort(port);
        ((JavaMailSenderImpl) this.javaMailSender).setUsername(user);
        ((JavaMailSenderImpl) this.javaMailSender).setPassword(password);
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
