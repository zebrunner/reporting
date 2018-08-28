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
package com.qaprosoft.zafira.ws.controller.application;

import java.util.List;

import javax.validation.Valid;

import com.qaprosoft.zafira.ws.controller.AbstractController;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.View;
import com.qaprosoft.zafira.models.dto.ViewType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.ViewService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "Views API")
@CrossOrigin
@RequestMapping("api/views")
public class ViewsAPIController extends AbstractController
{

	@Autowired
	private Mapper mapper;

	@Autowired
	private ViewService viewService;

	@ResponseStatusDetails
	@ApiOperation(value = "Get view", nickname = "getViewById", code = 200, httpMethod = "GET", response = View.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody View getViewById(@PathVariable(value = "id") long id) throws ServiceException
	{
		return viewService.getViewById(id);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get all views", nickname = "getAllViews", code = 200, httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<View> getAllViews(@RequestParam(value = "projectId", required = false) Long projectId)
			throws ServiceException
	{
		return viewService.getAllViews(projectId);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create view", nickname = "createView", code = 200, httpMethod = "POST", response = ViewType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_TEST_RUN_VIEWS')")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ViewType createView(@RequestBody @Valid ViewType view) throws ServiceException
	{
		return mapper.map(viewService.createView(mapper.map(view, View.class)), ViewType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update view", nickname = "updateView", code = 200, httpMethod = "PUT", response = ViewType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_TEST_RUN_VIEWS')")
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ViewType updateView(@RequestBody @Valid ViewType view) throws ServiceException
	{
		return mapper.map(viewService.updateView(mapper.map(view, View.class)), ViewType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete view", nickname = "deleteViewById", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_TEST_RUN_VIEWS')")
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void deleteView(@PathVariable(value = "id") long id) throws ServiceException
	{
		viewService.deleteViewById(id);
	}
}
