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
package com.qaprosoft.zafira.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qaprosoft.zafira.models.db.application.Setting;

import java.util.List;

/**
 * Created by irina on 23.8.17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConnectedToolType extends AbstractType {


    private static final long serialVersionUID = -5862684391407534486L;

    private String name;
    private List<Setting> settingList;
    private boolean isConnected;

    public List<Setting> getSettingList() {
        return settingList;
    }

    public void setSettingList(List<Setting> settingList) {
        this.settingList = settingList;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
