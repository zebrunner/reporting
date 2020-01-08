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
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.models.db.LauncherPreset;
import com.qaprosoft.zafira.models.dto.LauncherPresetDTO;
import com.qaprosoft.zafira.service.LauncherPresetService;
import com.qaprosoft.zafira.web.documented.LauncherPresetDocumentedController;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin
@RequestMapping(path = "api/launchers/{launcherId}/presets", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class LauncherPresetAPIController extends AbstractController implements LauncherPresetDocumentedController {

    private final LauncherPresetService launcherPresetService;
    private final Mapper mapper;

    public LauncherPresetAPIController(LauncherPresetService launcherPresetService, Mapper mapper) {
        this.launcherPresetService = launcherPresetService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PostMapping()
    @Override
    public LauncherPresetDTO createLauncherPreset(@RequestBody @Valid LauncherPresetDTO launcherPresetDTO, @PathVariable("launcherId") Long launcherId) {
        LauncherPreset launcherPreset = mapper.map(launcherPresetDTO, LauncherPreset.class);
        launcherPreset = launcherPresetService.create(launcherPreset, launcherId);
        launcherPresetDTO = mapper.map(launcherPreset, LauncherPresetDTO.class);
        return launcherPresetDTO;
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @GetMapping("/{id}/webhook")
    @Override
    public String buildWebHookUrl(
            @PathVariable("id") Long id,
            @PathVariable("launcherId") Long launcherId,
            @RequestParam(name = "providerId", required = false) Long providerId
    ) {
        return launcherPresetService.buildWebHookUrl(id, launcherId, providerId);
    }

    @PreAuthorize("hasPermission('MODIFY_LAUNCHERS')")
    @PutMapping("/{id}")
    @Override
    public LauncherPresetDTO updateLauncherPreset(@RequestBody @Valid LauncherPresetDTO launcherPresetDTO, @PathVariable("id") Long id, @PathVariable("launcherId") Long launcherId) {
        LauncherPreset launcherPreset = mapper.map(launcherPresetDTO, LauncherPreset.class);
        launcherPreset.setId(id);
        launcherPreset = launcherPresetService.update(launcherPreset, launcherId);
        return mapper.map(launcherPreset, LauncherPresetDTO.class);
    }
}
