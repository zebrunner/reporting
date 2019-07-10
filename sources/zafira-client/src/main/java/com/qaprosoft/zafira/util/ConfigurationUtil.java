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

import com.qaprosoft.zafira.config.CiConfig;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.MergeCombiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static com.qaprosoft.zafira.client.ClientDefaults.ZAFIRA_PROPERTIES_FILE;

public class ConfigurationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationUtil.class);

    private static final String ERR_MSG_INIT_CONFIG = "Unable to initialize a configuration '%s'";

    private static CombinedConfiguration configuration;

    public static CiConfig retrieveCiConfig(CombinedConfiguration config) {
        return new CiConfig.Builder()
                .setCiRunId(config.getString("ci_run_id", UUID.randomUUID().toString()))
                .setCiUrl(config.getString("ci_url", "http://localhost:8080/job/unavailable"))
                .setCiBuild(config.getString("ci_build", null))
                .setCiBuildCause(config.getString("ci_build_cause", "MANUALTRIGGER"))
                .setCiParentUrl(config.getString("ci_parent_url", null))
                .setCiParentBuild(config.getString("ci_parent_build", null))

                .setGitBranch(config.getString("git_branch", null))
                .setGitCommit(config.getString("git_commit", null))
                .setGitUrl(config.getString("git_url", null))
                .build();
    }

    public static CombinedConfiguration getConfiguration() {
        return getConfiguration(true);
    }

    public static CombinedConfiguration getConfiguration(boolean throwExceptionOnMissing) {
        if(configuration != null) {
            return configuration;
        }
        CombinedConfiguration config = new CombinedConfiguration(new MergeCombiner());
        try {
            config.setThrowExceptionOnMissing(throwExceptionOnMissing);
            config.addConfiguration(new SystemConfiguration());
            config.addConfiguration(getZafiraPropertiesConfiguration());
        } catch (ConfigurationException e) {
            String message = String.format(ERR_MSG_INIT_CONFIG, ZAFIRA_PROPERTIES_FILE);
            LOGGER.error(message, e);
        }
        configuration = config;
        return config;
    }

    public static void addSystemConfiguration(String key, String value) {
        System.setProperty(key, value);
        if(configuration != null) {
            configuration.addConfiguration(new SystemConfiguration());
        }
    }

    private static FileBasedConfiguration getZafiraPropertiesConfiguration() throws ConfigurationException {
        return new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(new Parameters().properties().setFileName(ZAFIRA_PROPERTIES_FILE))
                .getConfiguration();
    }

}
