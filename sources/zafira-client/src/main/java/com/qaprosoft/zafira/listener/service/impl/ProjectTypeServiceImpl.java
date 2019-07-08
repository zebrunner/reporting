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
package com.qaprosoft.zafira.listener.service.impl;

import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.listener.service.ProjectTypeService;

public class ProjectTypeServiceImpl implements ProjectTypeService {

    private final ZafiraClient zafiraClient;

    public ProjectTypeServiceImpl(ZafiraClient zafiraClient) {
        this.zafiraClient = zafiraClient;
    }

    @Override
    public void initProject(String projectName) {
        zafiraClient.initProject(projectName);
    }

}
