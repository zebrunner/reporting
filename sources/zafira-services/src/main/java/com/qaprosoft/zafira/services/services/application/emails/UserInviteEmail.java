package com.qaprosoft.zafira.services.services.application.emails;

import com.qaprosoft.zafira.models.db.Attachment;

import java.util.List;

public class UserInviteEmail implements IEmailMessage {

    private static final String SUBJECT = "New invite";

    private String token;
    private String zafiraLogoURL;
    private String companyLogoURL;
    private String workspaceURL;
    private String wsURL;

    public UserInviteEmail(String token, String zafiraLogoURL, String companyLogoURL, String workspaceURL, String wsURL) {
        this.token = token;
        this.zafiraLogoURL = zafiraLogoURL;
        this.companyLogoURL = companyLogoURL;
        this.workspaceURL = workspaceURL;
        this.wsURL = wsURL;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getZafiraLogoURL() {
        return zafiraLogoURL;
    }

    public void setZafiraLogoURL(String zafiraLogoURL) {
        this.zafiraLogoURL = zafiraLogoURL;
    }

    public String getCompanyLogoURL() {
        return companyLogoURL;
    }

    public void setCompanyLogoURL(String companyLogoURL) {
        this.companyLogoURL = companyLogoURL;
    }

    public String getWorkspaceURL() {
        return workspaceURL;
    }

    public void setWorkspaceURL(String workspaceURL) {
        this.workspaceURL = workspaceURL;
    }

    public String getWsURL() {
        return wsURL;
    }

    public void setWsURL(String wsURL) {
        this.wsURL = wsURL;
    }

    @Override
    public String getSubject() {
        return SUBJECT;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public EmailType getType() {
        return EmailType.USER_INVITE;
    }

    @Override
    public List<Attachment> getAttachments() {
        return null;
    }
}
