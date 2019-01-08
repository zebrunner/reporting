/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.models.db;

public class ScmAccount extends AbstractEntity {

    private static final long serialVersionUID = 7205460418919094068L;

    private String accessToken;
    private String organizationName;
    private String repositoryName;
    private String avatarURL;
    private String repositoryURL;
    private Long userId;
    private Name name;

    public enum Name {
        GITHUB
    }

    public ScmAccount() {
    }

    public ScmAccount(String accessToken, Name name, Long userId) {
        this.accessToken = accessToken;
        this.name = name;
    }

    public ScmAccount(String organizationName, String repositoryName) {
        this.organizationName = organizationName;
        this.repositoryName = repositoryName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getRepositoryURL() {
        return repositoryURL;
    }

    public void setRepositoryURL(String repositoryURL) {
        this.repositoryURL = repositoryURL;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return (this.organizationName + this.repositoryURL).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ScmAccount && this.hashCode() == ((ScmAccount) obj).hashCode();
    }
    
    public String buildAuthorizedURL() {
    		String[] urlSlices = repositoryURL.split("//");
        return urlSlices[0] + "//" + accessToken + ":" + urlSlices[1];
    }
}
