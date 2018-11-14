package com.qaprosoft.zafira.services.services.application.emails;

public enum EmailType {

    USER_INVITE("user_invite.ftl"), DASHBOARD("dashboard.ftl"), FORGOT_PASSWORD("forgot_password.ftl"), FORGOT_PASSWORD_LDAP("forgot_password_ldap.ftl"), MONITOR("monitor_status.ftl"), TEST_RUN("test_run_results.ftl");

    private final String templateName;

    EmailType(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }
}
