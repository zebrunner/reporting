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

import com.qaprosoft.zafira.models.db.View;
import com.qaprosoft.zafira.models.dto.ViewType;
import com.qaprosoft.zafira.service.ViewService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api("Views API")
@CrossOrigin
@RequestMapping(path = "api/views", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class ViewController extends AbstractController {

    private final Mapper mapper;
    private final ViewService viewService;

    public ViewController(Mapper mapper, ViewService viewService) {
        this.mapper = mapper;
        this.viewService = viewService;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves a view by its id", nickname = "getViewById", httpMethod = "GET", response = View.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('VIEW_TEST_RUN_VIEWS', 'MODIFY_TEST_RUN_VIEWS')")
    @GetMapping("/{id}")
    public View getViewById(@PathVariable("id") long id) {
        return viewService.getViewById(id);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves all views", nickname = "getAllViews", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('VIEW_TEST_RUN_VIEWS', 'MODIFY_TEST_RUN_VIEWS')")
    @GetMapping()
    public List<View> getAllViews(@RequestParam(value = "projectId", required = false) Long projectId) {
        return viewService.getAllViews(projectId);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Creates a view", nickname = "createView", httpMethod = "POST", response = ViewType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_TEST_RUN_VIEWS')")
    @PostMapping()
    public ViewType createView(@RequestBody @Valid ViewType view) {
        return mapper.map(viewService.createView(mapper.map(view, View.class)), ViewType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Updates a view", nickname = "updateView", httpMethod = "PUT", response = ViewType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_TEST_RUN_VIEWS')")
    @PutMapping()
    public ViewType updateView(@RequestBody @Valid ViewType view) {
        return mapper.map(viewService.updateView(mapper.map(view, View.class)), ViewType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Deletes a view", nickname = "deleteViewById", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_TEST_RUN_VIEWS')")
    @DeleteMapping("/{id}")
    public void deleteView(@PathVariable("id") long id) {
        viewService.deleteViewById(id);
    }

}
