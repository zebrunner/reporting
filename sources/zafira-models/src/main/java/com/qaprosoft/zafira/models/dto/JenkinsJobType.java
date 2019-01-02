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
package com.qaprosoft.zafira.models.dto;

import java.io.Serializable;
import java.util.Map;

public class JenkinsJobType implements Serializable {

    private static final long serialVersionUID = 4812574658258748772L;

    private String suite;
    private String url;
    private Long scmAccountId;
    private String branch;
    private Map<String, String> args;

    public JenkinsJobType(String suite, String url, Long scmAccountId, String branch) {
        this.suite = suite;
        this.url = url;
        this.scmAccountId = scmAccountId;
        this.branch = branch;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getScmAccountId() {
        return scmAccountId;
    }

    public void setScmAccountId(Long scmAccountId) {
        this.scmAccountId = scmAccountId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }
}
