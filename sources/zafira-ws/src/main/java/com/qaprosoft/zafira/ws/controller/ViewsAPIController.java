package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.models.db.View;
import com.qaprosoft.zafira.models.dto.ViewType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.ViewService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@Controller
@Api(value = "Views API")
@CrossOrigin
@RequestMapping("api/views")
public class ViewsAPIController extends AbstractController {

    @Autowired
    private Mapper mapper;

    @Autowired
    private ViewService viewService;

    @ResponseStatusDetails
    @ApiOperation(value = "Get view", nickname = "getViewById", code = 200, httpMethod = "GET", response = View.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    View getViewById(@PathVariable(value="id") long id) throws ServiceException
    {
        return viewService.getViewById(id);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all views", nickname = "getAllViews", code = 200, httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<View> getAllViews(@RequestParam(value="projectId", required=false) Long projectId) throws ServiceException
    {
        return viewService.getAllViews(projectId);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Create view", nickname = "createView", code = 200, httpMethod = "POST", response = ViewType.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ViewType createView(@RequestBody @Valid ViewType view) throws ServiceException
    {
        return mapper.map(viewService.createView(mapper.map(view, View.class)), ViewType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update view", nickname = "updateView", code = 200, httpMethod = "PUT", response = ViewType.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ViewType updateView(@RequestBody @Valid ViewType view) throws ServiceException
    {
        return mapper.map(viewService.updateView(mapper.map(view, View.class)), ViewType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete view", nickname = "deleteViewById", code = 200, httpMethod = "DELETE")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="{id}", method = RequestMethod.DELETE)
    public void deleteView(@PathVariable(value="id") long id) throws ServiceException
    {
        viewService.deleteViewById(id);
    }
}
