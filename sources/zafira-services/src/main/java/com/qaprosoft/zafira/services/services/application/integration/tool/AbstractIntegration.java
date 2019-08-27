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
package com.qaprosoft.zafira.services.services.application.integration.tool;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.AbstractIntegrationAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.adapter.GroupAdapter;
import com.qaprosoft.zafira.services.services.application.integration.tool.context.proxy.AbstractProxy;

public abstract class AbstractIntegration<T extends GroupAdapter>/* implements Integration<T>*/ {

    private static final String ERR_MSG_ADAPTER_NOT_FOUND_BY_ID = "Adapter for tenant '%s' with id '%d' not found";
    private static final String ERR_MSG_DEFAULT_ADAPTER_NOT_FOUND_BY_TYPE = "Default adapter for tenant '%s' with type '%s' not found";

    private final String defaultType;

    public AbstractIntegration(String defaultType) {
        this.defaultType = defaultType;
    }

    public boolean isEnabledAndConnected(Long id) {
        AbstractIntegrationAdapter adapter = id != null ? ((AbstractIntegrationAdapter) getAdapterById(id)) : ((AbstractIntegrationAdapter) getDefaultAdapter());
        return adapter.getIntegration().isEnabled() && adapter.isConnected();
    }

    public boolean isEnabledAndConnected() {
        return isEnabledAndConnected(null);
    }

    @SuppressWarnings("unchecked")
    public T getAdapterById(Long id) {
        return (T) AbstractProxy.getAdapter(id)
                                .orElseThrow(() -> new UnsupportedOperationException(String.format(ERR_MSG_ADAPTER_NOT_FOUND_BY_ID, TenancyContext.getTenantName(), id)));
    }

    @SuppressWarnings("unchecked")
    public T getDefaultAdapter() {
        return (T) AbstractProxy.getDefaultAdapter(defaultType)
                                .orElseThrow(() -> new UnsupportedOperationException(String.format(ERR_MSG_DEFAULT_ADAPTER_NOT_FOUND_BY_TYPE, TenancyContext.getTenantName(), defaultType)));
    }

    /*@Override
    public void init() {
        if (settingsService != null && contextClass != null) {
            try {
                List<Setting> settings = settingsService.getSettingsByTool(tool);

                Setting enabledSetting = getEnabledSetting(settings);
                boolean enabled = Boolean.valueOf(enabledSetting.getValue());

                // skip initialisation if tool is disabled
                if (enabled) {
                    boolean hasBinarySetting = hasBinarySetting(settings);
                    Map<Setting.SettingType, Object> mappedSettings = new HashMap<>();
                    settings.forEach(setting -> {
                        Setting.SettingType toolSetting = Setting.SettingType.valueOf(setting.getName());
                        if (toolSetting.isRequired() && StringUtils.isBlank(setting.getValue())) {
                            removeContext();
                            throw new IntegrationException("Integration tool '" + tool + "' data is malformed. Setting '" + setting.getName() + "' is required");
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
                LOGGER.error("Unable to initialize '" + tool + "' settings", e);
            }
        }
    }

    private Setting getEnabledSetting(List<Setting> settings) {
        return settings.stream()
                       .filter(setting -> setting.getName().endsWith("_ENABLED")) // it is assumed that there's exactly one setting indicating if tool is enabled
                       .findFirst()
                       .orElseThrow(() -> new IntegrationException("Integration tool '" + tool + "' data is malformed. 'Enabled' property is not set"));
    }*/

    /*private String mapEncrypted(Setting setting) {
        return setting.isEncrypted() ? cryptoService.decrypt(setting.getValue()) : setting.getValue();
    }

    private boolean hasBinarySetting(List<Setting> settings) {
        return settings.stream().anyMatch(setting -> !ArrayUtils.isEmpty(setting.getFile()));
    }*/

}
