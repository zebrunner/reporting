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
package com.qaprosoft.zafira.services.services.application.scm;

import com.qaprosoft.zafira.models.dto.scm.Organization;
import com.qaprosoft.zafira.models.dto.scm.Repository;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.util.GitHubHttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GitHubService implements IScmService {

    @Autowired
    private GitHubHttpUtils gitHubHttpUtils;

    @Value(value = "${zafira.scm.github.client.id}")
    private String gitHubClientId;

    @Value(value = "${zafira.scm.github.client.secret}")
    private String gitHubSecret;

    public String getAccessToken(String code) throws IOException, URISyntaxException {
        return this.gitHubHttpUtils.getAccessToken(code, this.gitHubClientId, this.gitHubSecret);
    }

    @Override
    public List<Repository> getRepositories(String accessToken, String organizationName) throws IOException, ServiceException {
        GitHub gitHub = GitHub.connectUsingOAuth(accessToken);
        GHPerson person = StringUtils.isBlank(organizationName) ? gitHub.getMyself() : gitHub.getOrganization(organizationName);
        return person.listRepositories().asList().stream().map(repository -> {
            Repository repo = new Repository(repository.getName());
            repo.setPrivate(repository.isPrivate());
            repo.setUrl(repository.getHtmlUrl().toString());
            return repo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Organization> getOrganizations(String accessToken) throws IOException, ServiceException {
        GitHub gitHub = GitHub.connectUsingOAuth(accessToken);
        List<Organization> organizations = gitHub.getMyself().getAllOrganizations().stream().map(organization -> {
            Organization result = new Organization(organization.getLogin());
            result.setAvatarURL(organization.getAvatarUrl());
            return result;
        }).collect(Collectors.toList());
        Organization myself = new Organization(gitHub.getMyself().getLogin());
        myself.setAvatarURL(gitHub.getMyself().getAvatarUrl());
        organizations.add(myself);
        return organizations;
    }

    @Override
    public String getClientId() {
        return this.gitHubClientId;
    }
}
