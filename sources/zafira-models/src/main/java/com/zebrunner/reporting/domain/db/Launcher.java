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
package com.zebrunner.reporting.domain.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Launcher extends AbstractEntity {

    private static final long serialVersionUID = 7864420961256586573L;

    private String name;
    private String model;
    private ScmAccount scmAccount;
    private Job job;
    private boolean autoScan;
    private List<LauncherPreset> presets;
    private String type;

    public Launcher(String name, String model, ScmAccount scmAccount, Job job, String type, boolean autoScan) {
        this.name = name;
        this.model = model;
        this.scmAccount = scmAccount;
        this.job = job;
        this.type = type;
        this.autoScan = autoScan;
    }
}
