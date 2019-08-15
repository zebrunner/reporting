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
package com.qaprosoft.zafira.services.services.application.integration;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.IntegrationException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.context.AbstractContext;
import com.qaprosoft.zafira.services.services.application.integration.context.AdditionalProperty;
import com.qaprosoft.zafira.services.services.application.integration.impl.CryptoService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractIntegration<T extends AbstractContext> implements Integration<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractIntegration.class);

    private final SettingsService settingsService;
    private final CryptoService cryptoService;
    private final Setting.Tool tool;
    private Class<T> contextClass;

    public AbstractIntegration(Setting.Tool tool) {
        this(null, null, tool, null);
    }

    public AbstractIntegration(SettingsService settingsService, Setting.Tool tool, Class<T> contextClass) {
        this(settingsService, null, tool, contextClass);
    }

    public AbstractIntegration(SettingsService settingsService, CryptoService cryptoService, Setting.Tool tool, Class<T> contextClass) {
        this.settingsService = settingsService;
        this.cryptoService = cryptoService;
        this.tool = tool;
        this.contextClass = contextClass;
    }

    @Override
    public void init() {
        if (settingsService != null && contextClass != null) {
            try {
                List<Setting> settings = settingsService.getSettingsByTool(tool);

                boolean enabled = true;
                // crypto is the only tool without enabled/disabled indication
                if (!Setting.Tool.CRYPTO.equals(tool)) {
                    Setting enabledSetting = getEnabledSetting(settings);
                    enabled = Boolean.valueOf(enabledSetting.getValue());
                }

                // skip initialisation if tool is disabled
                if (enabled) {
                    boolean hasBinarySetting = hasBinarySetting(settings);
                    Map<Setting.SettingType, Object> mappedSettings = new HashMap<>();
                    settings.forEach(setting -> {
                        Setting.SettingType toolSetting = Setting.SettingType.valueOf(setting.getName());
                        if (toolSetting.isRequired() && StringUtils.isBlank(setting.getValue())) {
                            removeContext();
                            throw new IntegrationException("Integration tool '" + tool + "' data is malformed for tenant : [" + TenancyContext.getTenantName() + "]. Setting '" + setting.getName() + "' is required");
                        }
                        if (setting.isEncrypted()) {
                            setting.setValue(mapEncrypted(setting));
                        }
                        if (!ArrayUtils.isEmpty(setting.getFile())) {
                            mappedSettings.put(toolSetting, setting);
                        }
                        Object value = hasBinarySetting ? setting : setting.getValue();
                        mappedSettings.put(toolSetting, value);
                    });
                    T context = createContextInstance(mappedSettings);
                    putContext(context);
                }
            } catch (InstantiationException e) {
                throw new IntegrationException(e.getMessage(), e);
            } catch (Exception e) {
                LOGGER.error("Unable to initialize '" + tool + "' settings for tenant: [" + TenancyContext.getTenantName() + "]", e);
            }
        }
    }

    private Setting getEnabledSetting(List<Setting> settings) {
        return settings.stream()
                       .filter(setting -> setting.getName().endsWith("_ENABLED")) // it is assumed that there's exactly one setting indicating if tool is enabled
                       .findFirst()
                       .orElseThrow(() -> new IntegrationException("Integration tool '" + tool + "' data is malformed. 'Enabled' property is not set"));
    }

    @Override
    public Map<? extends AdditionalProperty, String> additionalContextProperties() {
        return null;
    }

    private T createContextInstance(Map<Setting.SettingType, Object> settings) throws InstantiationException {
        T context = null;
        try {
            Map<? extends AdditionalProperty, String> additionalProperties = additionalContextProperties();
            if (additionalProperties != null) {
                Constructor<T> mainConstructor = contextClass.getConstructor(Map.class, Map.class);
                context = mainConstructor.newInstance(settings, additionalProperties);
            } else {
                Constructor<T> mainConstructor = contextClass.getConstructor(Map.class);
                context = mainConstructor.newInstance(settings);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new InstantiationException("Cannot create context instance. " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return context;
    }

    private String mapEncrypted(Setting setting) {
        return setting.isEncrypted() ? cryptoService.decrypt(setting.getValue()) : setting.getValue();
    }

    private boolean hasBinarySetting(List<Setting> settings) {
        return settings.stream().anyMatch(setting -> !ArrayUtils.isEmpty(setting.getFile()));
    }

    @Override
    public Setting.Tool getTool() {
        return tool;
    }

}
