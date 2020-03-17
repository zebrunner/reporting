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
package com.zebrunner.reporting.service.integration.tool.proxy;

import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.integration.IntegrationGroupService;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.service.integration.tool.adapter.IntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.AerokubeAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.BrowserStackAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.LambdaTestAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.MCloudAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.SauceLabsAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.SeleniumAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.testautomationtool.ZebrunnerAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestAutomationToolProxy extends IntegrationAdapterProxy {

    private static final Map<String, Class<? extends IntegrationAdapter>> INTEGRATION_TYPE_ADAPTERS = Map.of(
        "SELENIUM", SeleniumAdapter.class,
        "ZEBRUNNER", ZebrunnerAdapter.class,
        "BROWSERSTACK", BrowserStackAdapter.class,
        "MCLOUD", MCloudAdapter.class,
        "SAUCELABS", SauceLabsAdapter.class,
        "AEROKUBE", AerokubeAdapter.class,
        "LAMBDATEST", LambdaTestAdapter.class
    );

    public TestAutomationToolProxy(
            ApplicationContext applicationContext,
            IntegrationGroupService integrationGroupService,
            IntegrationService integrationService,
            CryptoService cryptoService
    ) {
        super(applicationContext, integrationGroupService, integrationService, cryptoService, "TEST_AUTOMATION_TOOL", INTEGRATION_TYPE_ADAPTERS);
    }
}
