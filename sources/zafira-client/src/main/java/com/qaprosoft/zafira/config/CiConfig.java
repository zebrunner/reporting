/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
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
 *******************************************************************************/
package com.qaprosoft.zafira.config;

import org.apache.commons.lang3.StringUtils;

/**
 * Simple configuration bean that represent CI configuration properties used to initialize Zafira test runs.
 *
 * @author akhursevich
 */
public class CiConfig {

    private String ciRunId;
    private String ciUrl;
    private Integer ciBuild;
    private BuildCase ciBuildCause;
    private String ciParentUrl;
    private Integer ciParentBuild;

    private String gitBranch;
    private String gitCommit;
    private String gitUrl;

    public enum BuildCase {
        UPSTREAMTRIGGER,
        TIMERTRIGGER,
        MANUALTRIGGER,
        SCMTRIGGER
    }

    private CiConfig() {
    }

    private CiConfig(String ciRunId, String ciUrl, Integer ciBuild, BuildCase ciBuildCause, String ciParentUrl, Integer ciParentBuild,
                     String gitBranch, String gitCommit, String gitUrl) {
        this.ciRunId = ciRunId;
        this.ciUrl = ciUrl;
        this.ciBuild = ciBuild;
        this.ciBuildCause = ciBuildCause;
        this.ciParentUrl = ciParentUrl;
        this.ciParentBuild = ciParentBuild;
        this.gitBranch = gitBranch;
        this.gitCommit = gitCommit;
        this.gitUrl = gitUrl;
    }

    public static class Builder {

        private String ciRunId;
        private String ciUrl;
        private Integer ciBuild;
        private BuildCase ciBuildCause;
        private String ciParentUrl;
        private Integer ciParentBuild;

        private String gitBranch;
        private String gitCommit;
        private String gitUrl;

        public Builder setCiRunId(String ciRunId) {
            this.ciRunId = ciRunId;
            return this;
        }

        public Builder setCiUrl(String ciUrl) {
            this.ciUrl = StringUtils.removeEnd(ciUrl, "/");
            return this;
        }

        public Builder setCiBuild(String ciBuild) {
            this.ciBuild = StringUtils.isEmpty(ciBuild) ? 0 : Integer.valueOf(ciBuild);
            return this;
        }

        public Builder setCiBuildCause(String ciBuildCause) {
            if (ciBuildCause != null) {
                // HotFix for 'BuildCase.UPSTREAMTRIGGER,UPSTREAMTRIGGER,UPSTREAMTRIGGER'
                this.ciBuildCause = BuildCase.valueOf(ciBuildCause.toUpperCase().split(",")[0]);
            }
            return this;
        }

        public Builder setCiParentUrl(String ciParentUrl) {
            this.ciParentUrl = StringUtils.removeEnd(ciParentUrl, "/");
            return this;
        }

        public Builder setCiParentBuild(String ciParentBuild) {
            this.ciParentBuild = StringUtils.isEmpty(ciParentBuild) ? 0 : Integer.valueOf(ciParentBuild);
            return this;
        }

        public Builder setGitBranch(String gitBranch) {
            this.gitBranch = gitBranch;
            return this;
        }

        public Builder setGitCommit(String gitCommit) {
            this.gitCommit = gitCommit;
            return this;
        }

        public Builder setGitUrl(String gitUrl) {
            this.gitUrl = gitUrl;
            return this;
        }

        public CiConfig build() {
            return new CiConfig(ciRunId, ciUrl, ciBuild, ciBuildCause, ciParentUrl, ciParentBuild, gitBranch, gitCommit, gitUrl);
        }

    }

    public String getCiRunId() {
        return ciRunId;
    }

    public String getCiUrl() {
        return ciUrl;
    }

    public Integer getCiBuild() {
        return ciBuild;
    }

    public BuildCase getCiBuildCause() {
        return ciBuildCause;
    }

    public String getCiParentUrl() {
        return ciParentUrl;
    }

    public Integer getCiParentBuild() {
        return ciParentBuild;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public String getGitCommit() {
        return gitCommit;
    }

    public String getGitUrl() {
        return gitUrl;
    }

}