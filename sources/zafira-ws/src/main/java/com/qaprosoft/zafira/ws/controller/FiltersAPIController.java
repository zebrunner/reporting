package com.qaprosoft.zafira.ws.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.Filter;
import com.qaprosoft.zafira.models.dto.filter.FilterType;
import com.qaprosoft.zafira.models.dto.filter.Subject;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.FilterService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "Filters API")
@CrossOrigin
@RequestMapping("api/filters")
public class FiltersAPIController extends AbstractController
{

	@Autowired
	private FilterService filterService;

	@Autowired
	private Mapper mapper;

	@ResponseStatusDetails
	@ApiOperation(value = "Create filter", nickname = "createFilter", code = 200, httpMethod = "POST", response = FilterType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
    FilterType createFilter(@RequestBody @Valid FilterType filterType) throws ServiceException
	{
		if(filterService.getFilterByName(filterType.getName()) != null)
		{
			throw new ServiceException("Filter with name '" + filterType.getName() + "' already exists");
		}
		if(filterType.isPublicAccess() && ! isAdmin())
		{
			filterType.setPublicAccess(false);
		}
		filterType.getSubject().sortCriterias();
		Filter filter = mapper.map(filterType, Filter.class);
		filter.setUserId(getPrincipalId());
		return mapper.map(filterService.createFilter(filter), FilterType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get all public filters", nickname = "getAllPublicFilters", code = 200, httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "all/public", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<FilterType> getAllPublicFilters() throws ServiceException
	{
		return filterService.getAllPublicFilters(getPrincipalId()).stream().map(filter -> mapper.map(filter, FilterType.class))
				.collect(Collectors.toList());
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Update filter", nickname = "updateFilter", code = 200, httpMethod = "PUT", response = FilterType.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("isOwner(@filterService.getFilterById(#filterType.id), 'userId')")
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
    FilterType updateFilter(@RequestBody @Valid FilterType filterType) throws ServiceException
	{
		filterType.getSubject().sortCriterias();
		return mapper.map(filterService.updateFilter(mapper.map(filterType, Filter.class), isAdmin()), FilterType.class);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete filter", nickname = "deleteFilter", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("isOwner(@filterService.getFilterById(#id), 'userId')")
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void deleteFilter(@PathVariable(value = "id") Long id) throws ServiceException
	{
		filterService.deleteFilterById(id);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get filter builder", nickname = "getBuilder", code = 200, httpMethod = "GET", response = Subject.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{name}/builder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
    Subject getBuilder(@PathVariable(value = "name")Subject.Name name)
	{
		return filterService.getStoredSubject(name);
	}
}
