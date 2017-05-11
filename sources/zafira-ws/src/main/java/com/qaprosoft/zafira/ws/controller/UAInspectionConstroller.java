package com.qaprosoft.zafira.ws.controller;

import javax.validation.Valid;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchResult;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.UAInspectionSearchCriteria;
import com.qaprosoft.zafira.models.db.ua.UAInspection;
import com.qaprosoft.zafira.models.dto.ua.UAInspectionType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.ua.UAInspectionService;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("uainspections")
public class UAInspectionConstroller extends AbstractController
{
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private UAInspectionService uaInspectionService;

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "index", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView openIndexPage()
	{
		return new ModelAndView("uainspections/index");
	}
	
	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST)
	public void createUAInspection(@RequestBody @Valid UAInspectionType uaInspection) throws ServiceException
	{
		uaInspectionService.createUAInspection(mapper.map(uaInspection, UAInspection.class));
	}

	@ApiIgnore
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SearchResult<UAInspection> searchUAInspections(@RequestBody UAInspectionSearchCriteria sc) throws ServiceException
	{
		return uaInspectionService.searchUAInspections(sc);
	}
}
