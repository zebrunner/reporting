package com.qaprosoft.zafira.models.db;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Invitation extends AbstractEntity {

    private static final long serialVersionUID = -7507603908818483927L;

    private String email;
    private String token;
    private Date expiresIn;

    public Invitation() {
    }

    public Invitation(String email, String token, Date expiresIn) {
        this.email = email;
        this.token = token;
        this.expiresIn = expiresIn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Date expiresIn) {
        this.expiresIn = expiresIn;
    }

    public boolean isExpired() {
        return getCurrentUTCDate().after(this.getExpiresIn());
    }

    private Date getCurrentUTCDate() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
    }
}
