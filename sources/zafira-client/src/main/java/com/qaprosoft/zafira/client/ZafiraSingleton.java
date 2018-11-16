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
package com.qaprosoft.zafira.client;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.tree.MergeCombiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qaprosoft.zafira.client.ZafiraClient.Response;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;

/**
 * ZafiraSingleton - singleton wrapper around {@link ZafiraClient}.
 * 
 * @author Alexey Khursevich (hursevich@gmail.com)
 */
public enum ZafiraSingleton {

	INSTANCE;

	private final Logger LOGGER = Logger.getLogger(ZafiraSingleton.class);
	
	private final String ZAFIRA_PROPERTIES = "zafira.properties";

	private ZafiraClient zc;

	private Boolean running = false;

	ZafiraSingleton() {
		try {
			CombinedConfiguration config = new CombinedConfiguration(new MergeCombiner());
			config.setThrowExceptionOnMissing(false);
			config.addConfiguration(new SystemConfiguration());
			config.addConfiguration(
					new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
							.configure(new Parameters().properties().setFileName(ZAFIRA_PROPERTIES))
							.getConfiguration());

			final boolean enabled = config.getBoolean("zafira_enabled", false);
			final String url = config.getString("zafira_service_url", StringUtils.EMPTY);
			final String token = config.getString("zafira_access_token", StringUtils.EMPTY);

			zc = new ZafiraClient(url);
			if (enabled && zc.isAvailable()) {
				Response<AuthTokenType> auth = zc.refreshToken(token);
				if (auth.getStatus() == 200) {
					zc.setAuthToken(auth.getObject().getType() + " " + auth.getObject().getAccessToken());
					this.running = true;

					this.zc.initAmazonS3Client();
					this.zc.initTenant();
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * @return {@link ZafiraClient} instance
	 */
	public ZafiraClient getClient() {
		return zc;
	}

	/**
	 * 
	 * @return Zafira integration status
	 */
	public Boolean isRunning() {
		return running;
	}
}