package com.qaprosoft.zafira.ws.security;

import org.jasypt.util.password.BasicPasswordEncryptor;

public class PasswordEncoder implements org.springframework.security.crypto.password.PasswordEncoder {

    private final BasicPasswordEncryptor passwordEncryptor;

    public PasswordEncoder() {
        this.passwordEncryptor = new BasicPasswordEncryptor();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncryptor.encryptPassword(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncryptor.checkPassword(rawPassword.toString(), encodedPassword);
    }

}
