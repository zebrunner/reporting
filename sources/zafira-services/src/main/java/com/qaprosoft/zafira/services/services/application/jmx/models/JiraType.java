/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.jmx.models;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.JiraClient;

@SuppressWarnings("deprecation")
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
