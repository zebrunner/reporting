package com.qaprosoft.zafira.services.services.jmx.models;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.JiraClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class JiraType extends AbstractType
{

    private BasicCredentials credentials;
    private JiraClient jiraClient;

    public JiraType(String url, String username, String password)
    {
        this.credentials = new BasicCredentials(username, password);
        this.jiraClient = new JiraClient(url, credentials);
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        ((DefaultHttpClient)getJiraClient().getRestClient().getHttpClient()).setParams(httpParams);
    }

    public BasicCredentials getCredentials()
    {
        return credentials;
    }

    public void setCredentials(BasicCredentials credentials)
    {
        this.credentials = credentials;
    }

    public JiraClient getJiraClient()
    {
        return jiraClient;
    }

    public void setJiraClient(JiraClient jiraClient)
    {
        this.jiraClient = jiraClient;
    }
}
