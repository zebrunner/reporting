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
package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.Launcher;

import java.util.List;

public interface LauncherMapper {

    void createLauncher(Launcher launcher);

    Launcher getLauncherById(Long id);

    Launcher getLauncherByJobId(Long jobId);

    List<Launcher> getAllLaunchers();

    void updateLauncher(Launcher launcher);

    void deleteLauncherById(Long id);

    void deleteAutoScannedLaunchersByScmAccountId(Long scmAccountId);

}
