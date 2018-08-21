package com.qaprosoft.zafira.services.services.jmx.models;

import com.offbytwo.jenkins.JenkinsServer;

import java.net.URI;
import java.net.URISyntaxException;

public class JenkinsType extends AbstractType
{

    private JenkinsServer jenkinsServer;

    public JenkinsType(String url, String username, String passwordOrApiToken) {
        try {
            this.jenkinsServer = new JenkinsServer(new URI(url), username, passwordOrApiToken);
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public JenkinsServer getJenkinsServer()
    {
        return jenkinsServer;
    }

    public void setJenkinsServer(JenkinsServer jenkinsServer)
    {
        this.jenkinsServer = jenkinsServer;
    }
}
