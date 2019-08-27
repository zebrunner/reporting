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
 ******************************************************************************//*

package com.qaprosoft.zafira.services.services.application.integration.tool;

import com.qaprosoft.zafira.models.db.Setting;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IntegrationService {

    private final Map<Setting.Tool, Integration> integrations;

    public IntegrationService(Map<String, Integration> integrations) {
        this.integrations = new HashMap<>();
        integrations.forEach((beanName, integration) -> this.integrations.put(integration.getTool(), integration));
    }

    @SuppressWarnings("unchecked")
    public <T extends Integration> T getServiceByTool(Setting.Tool tool) {
        return (T) integrations.get(tool);
    }
}
*/
