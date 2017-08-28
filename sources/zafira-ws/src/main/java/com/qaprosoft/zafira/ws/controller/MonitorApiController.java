package com.qaprosoft.zafira.ws.controller;


import com.qaprosoft.zafira.models.db.Group;
import com.qaprosoft.zafira.models.db.Monitor;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.MonitorService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(value = "Monitors API")
@CrossOrigin
@RequestMapping("api/monitors")
public class MonitorApiController extends AbstractController {

    @Autowired
    private MonitorService monitorsService;


    @ResponseStatusDetails
    @ApiOperation(value = "Create monitor", nickname = "createMonitor", code = 200, httpMethod = "POST", response = Monitor.class)
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Monitor createGroup(@RequestBody Monitor monitor) throws ServiceException
    {
        return monitorsService.createMonitor(monitor);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete monitor", nickname = "deleteMonitor", code = 200, httpMethod = "DELETE")
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void deleteMonitor(@PathVariable(value = "id") long id) throws ServiceException
    {
        monitorsService.deleteMonitorById(id);
    }


    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Update monitor", nickname = "updateMonitor", code = 200, httpMethod = "PUT", response = Group.class)
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Monitor updateGroup(@RequestBody Monitor monitor) throws ServiceException
    {
        return monitorsService.updateMonitor(monitor);
    }


    @ResponseStatusDetails
    @ApiOperation(value = "Get all monitors", nickname = "getAllMonitors", code = 200, httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Monitor> getAllMonitors() throws ServiceException
    {
        return monitorsService.getAllMonitors();
    }

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get monitor by id", nickname = "getMonitorById", code = 200, httpMethod = "GET", response = Monitor.class)
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Monitor getMonitorById(@PathVariable(value = "id") long id) throws ServiceException
    {
        return monitorsService.getMonitorById(id);
    }



    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get monitors count", nickname = "getMonitorsCount", code = 200, httpMethod = "GET", response = Integer.class)
    @RequestMapping(value = "count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Integer getMonitorsCount() throws ServiceException
    {
        return monitorsService.getMonitorsCount();
    }
}

