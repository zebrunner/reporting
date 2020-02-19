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
package com.qaprosoft.zafira.models.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.models.db.config.Argument;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TestConfig extends AbstractEntity {
    private static final long serialVersionUID = 5668009693004786533L;

    private String url;
    private String env;
    private String platform;
    private String platformVersion;
    private String browser;
    private String browserVersion;
    private String appVersion;
    private String locale;
    private String language;
    private String device;

    public TestConfig(long id) {
        super.setId(id);
    }

    public TestConfig(String platform) {
        this.platform = platform;
    }

    public TestConfig init(List<Argument> args) {
        for (Argument arg : args) {
            if ("url".equals(arg.getKey())) {
                this.url = arg.getValue();
            } else if ("env".equals(arg.getKey())) {
                this.env = arg.getValue();
            } else if ("platform".equals(arg.getKey())) {
                this.platform = arg.getValue();
            } else if ("platform_version".equals(arg.getKey())) {
                this.platformVersion = arg.getValue();
            } else if ("browser".equals(arg.getKey())) {
                this.browser = arg.getValue();
            } else if ("browser_version".equals(arg.getKey())) {
                this.browserVersion = arg.getValue();
            } else if ("app_version".equals(arg.getKey())) {
                this.appVersion = arg.getValue();
            } else if ("locale".equals(arg.getKey())) {
                this.locale = arg.getValue();
            } else if ("language".equals(arg.getKey())) {
                this.language = arg.getValue();
            } else if ("device".equals(arg.getKey())) {
                this.device = arg.getValue();
            }
        }
        return this;
    }

    public String buildPlatformName() {
        StringBuilder resultingPlatform = new StringBuilder();

        // Check if both platform and browser are present which means mobile browser
        if (!isEmpty(platform) && !isEmpty(browser)) {
            getResultingPlatform(resultingPlatform.append(platform)
                                                  .append("_"), browser, browserVersion);
            // The other way it's platform takes precedence
        } else if (!isEmpty(platform)) {
            getResultingPlatform(resultingPlatform, platform, platformVersion);
        } else {
            getResultingPlatform(resultingPlatform, browser, browserVersion);
        }

        return resultingPlatform.toString();
    }

    private void getResultingPlatform(StringBuilder resultingPlatform, String name, String version) {
        resultingPlatform.append(name)
                         .append(" ")
                         .append(version);
    }
}
