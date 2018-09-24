package com.qaprosoft.zafira.services.services.application.emails;

public enum EmailType {

    USER_INVITE("user_invite.ftl"), DASHBOARD("dashboard.ftl"), MONITOR("monitor_status.ftl"), TEST_RUN("test_run_results.ftl");

    private String templateName;

    EmailType(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }
}
