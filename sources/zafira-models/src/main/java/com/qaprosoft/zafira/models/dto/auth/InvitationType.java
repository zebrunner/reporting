package com.qaprosoft.zafira.models.dto.auth;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class InvitationType {

    @NotEmpty(message = "Email required")
    @Email(message = "Valid email required")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
