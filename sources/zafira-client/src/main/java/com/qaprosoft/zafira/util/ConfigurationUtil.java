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
package com.qaprosoft.zafira.util;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.MergeCombiner;
import org.apache.log4j.Logger;

import static com.qaprosoft.zafira.client.ClientDefaults.ZAFIRA_PROPERTIES_FILE;

public class ConfigurationUtil {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationUtil.class);

    private static final String ERR_MSG_INIT_CONFIG = "Unable to initialize a configuration";

    private static CombinedConfiguration configuration;

    public static CombinedConfiguration getConfiguration() {
        if(configuration != null) {
            return configuration;
        }
        CombinedConfiguration config = new CombinedConfiguration(new MergeCombiner());
        try {
            config.setThrowExceptionOnMissing(true);
            config.addConfiguration(new SystemConfiguration());
            config.addConfiguration(new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                    .configure(new Parameters().properties().setFileName(ZAFIRA_PROPERTIES_FILE)).getConfiguration());
        } catch (ConfigurationException e) {
            LOGGER.error(ERR_MSG_INIT_CONFIG, e);
        }
        configuration = config;
        return config;
    }

}
