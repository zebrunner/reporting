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
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.integration.context;

import com.github.seratch.jslack.Slack;
import com.qaprosoft.zafira.models.db.Setting;

import java.util.Map;

public class SlackContext extends AbstractContext {

    private final String webHookUrl;
    private final Slack slack;

    public SlackContext(Map<Setting.SettingType, String> settings) {
        super(settings, settings.get(Setting.SettingType.SLACK_ENABLED));

        this.webHookUrl = settings.get(Setting.SettingType.SLACK_WEB_HOOK_URL);
        this.slack = Slack.getInstance();
    }

    public String getWebHookUrl() {
        return webHookUrl;
    }

    public Slack getSlack() {
        return slack;
    }
}
