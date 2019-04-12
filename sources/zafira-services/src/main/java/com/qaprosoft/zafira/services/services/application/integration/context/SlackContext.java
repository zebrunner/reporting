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
package com.qaprosoft.zafira.services.services.application.integration.context;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.application.integration.AdditionalProperty;
import in.ashwanthkumar.slack.webhook.Slack;

import java.util.Map;

import static com.qaprosoft.zafira.services.services.application.integration.context.SlackContext.SlackAdditionalProperty.AUTHOR;
import static com.qaprosoft.zafira.services.services.application.integration.context.SlackContext.SlackAdditionalProperty.IMAGE;

public class SlackContext extends AbstractContext
{

    private Slack slack;

    public SlackContext(Map<Setting.SettingType, String> settings, Map<SlackAdditionalProperty, String> additionalSettings)
    {
        super(settings, settings.get(Setting.SettingType.SLACK_ENABLED));

        String webHook = settings.get(Setting.SettingType.SLACK_WEB_HOOK_URL);
        String author = additionalSettings.get(AUTHOR);
        String picPath = additionalSettings.get(IMAGE);

        this.slack = new Slack(webHook);
        this.slack = this.slack.displayName(author);
        this.slack = this.slack.icon(picPath);
    }

    public enum SlackAdditionalProperty implements AdditionalProperty {
        AUTHOR, IMAGE
    }

    public Slack getSlack()
    {
        return slack;
    }

    public void setSlack(Slack slack)
    {
        this.slack = slack;
    }
}
