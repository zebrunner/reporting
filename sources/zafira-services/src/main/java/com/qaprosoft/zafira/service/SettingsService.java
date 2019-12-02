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
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.SettingsMapper;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.service.bean.RabbitMQConfigBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SettingsService {

    private final SettingsMapper settingsMapper;
    private final ElasticsearchService elasticsearchService;
    private final RabbitMQConfigBean rabbitMQConfigBean;

    public SettingsService(SettingsMapper settingsMapper, ElasticsearchService elasticsearchService, RabbitMQConfigBean rabbitMQConfigBean) {
        this.settingsMapper = settingsMapper;
        this.elasticsearchService = elasticsearchService;
        this.rabbitMQConfigBean = rabbitMQConfigBean;
    }

    @Transactional(readOnly = true)
    public Setting getSettingByName(String name) {
        return settingsMapper.getSettingByName(name);
    }

    @Transactional(rollbackFor = Exception.class)
    public Setting updateSetting(Setting setting) {
        //setting.setValue(StringUtils.isBlank(setting.getValue() != null ? setting.getValue().trim() : null) ? null : setting.getValue());
        settingsMapper.updateSetting(setting);
        return setting;
    }

    @Transactional(readOnly = true)
    public String getPostgresVersion() {
        return settingsMapper.getPostgresVersion();
    }

    public List<Setting> getSystemSettings(String tool) {
        if (tool.equalsIgnoreCase("ELASTICSEARCH")) {
            return elasticsearchService.getSettings();
        } else if (tool.equalsIgnoreCase("RABBITMQ")) {
            return List.of(
                    new Setting("RABBITMQ_HOST", rabbitMQConfigBean.getHost()),
                    new Setting("RABBITMQ_PORT", Integer.toString(rabbitMQConfigBean.getPort())),
                    new Setting("RABBITMQ_USER", rabbitMQConfigBean.getClientUser()),
                    new Setting("RABBITMQ_PASSWORD", rabbitMQConfigBean.getClientPasscode()),
                    new Setting("RABBITMQ_ENABLED", Boolean.toString(true))
            );
        } else {
            throw new RuntimeException(String.format("Unsupported tool %s, this API should not be used for anything but ElasticSearch or Rabbit", tool));
        }
    }

}
