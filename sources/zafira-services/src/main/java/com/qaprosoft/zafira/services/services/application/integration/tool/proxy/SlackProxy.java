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
package com.qaprosoft.zafira.services.services.application.integration.tool.proxy;

import com.qaprosoft.zafira.services.services.application.CryptoService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationGroupService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.tool.adapter.IntegrationAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.adapter.slack.SlackIntegrationAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SlackProxy extends IntegrationAdapterProxy {

    private static final Map<String, Class<? extends IntegrationAdapter>> INTEGRATION_TYPE_ADAPTERS = Map.of(
            "SLACK", SlackIntegrationAdapter.class
    );

    public SlackProxy(ApplicationContext applicationContext,
                      IntegrationGroupService integrationGroupService,
                      IntegrationService integrationService,
                      CryptoService cryptoService,
                      @Value("${zafira.slack.image-url}") String image,
                      @Value("${zafira.slack.author}") String author
    ) {
        super(applicationContext, integrationGroupService, integrationService, cryptoService, "SLACK", INTEGRATION_TYPE_ADAPTERS, Map.of("image", image, "author", author));
    }
}
