package com.zebrunner.reporting.service.email;

public enum EmailType {

    USER_INVITE("invitation.ftl"),
    USER_INVITE_LDAP("invitation_ldap.ftl"),
    DASHBOARD("dashboard.ftl"),
    FORGOT_PASSWORD("forgot_password.ftl"),
    FORGOT_PASSWORD_LDAP("forgot_password_ldap.ftl"),
    TEST_RUN("test_run_results.ftl");

    private final String templateName;

    EmailType(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }
}
