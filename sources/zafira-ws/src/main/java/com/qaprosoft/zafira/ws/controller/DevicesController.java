package com.qaprosoft.zafira.ws.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.qaprosoft.zafira.models.db.Device;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.DeviceService;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping("devices")
public class DevicesController extends AbstractController
{
	@Autowired
	private DeviceService deviceSerivce;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "index", method = RequestMethod.GET)
	public ModelAndView openDevicesPage()
	{
		return new ModelAndView("devices/index");
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Device> getAllDevices() throws ServiceException
	{
		return deviceSerivce.getAllDevices();
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	public void deleteDevice(@PathVariable(value="id") long id) throws ServiceException
	{
		deviceSerivce.deleteDeviceById(id);
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="sync", method = RequestMethod.PUT)
	public void sync() throws ServiceException
	{
		deviceSerivce.syncDevicesWithSTF();
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Device createDevice(@RequestBody Device device) throws ServiceException
	{
		return deviceSerivce.createDevice(device);
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Device editDevice(@RequestBody Device device) throws ServiceException
	{
		return deviceSerivce.updateDevice(device);
	}
}
