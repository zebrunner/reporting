package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Api(value = "Download files API")
@Controller
@CrossOrigin
@RequestMapping("api/download")
public class DownloadAPIController extends AbstractController
{

	private static final String DATA_FOLDER = "/opt/apk/%s";

	@Autowired
	private ServletContext context;

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Download file by filename", nickname = "downloadFile", httpMethod = "GET")
	@RequestMapping(method = RequestMethod.GET)
	public void downloadFile(HttpServletResponse response, @RequestParam(value = "filename") String filename)
			throws IOException
	{
		File file = new File(String.format(DATA_FOLDER, filename));
		String mimeType = context.getMimeType(file.getPath());
		if (mimeType == null)
		{
			mimeType = "application/octet-stream";
		}
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
		response.setContentLength((int)file.length());
		FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
		response.flushBuffer();
	}

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Check file is present in file system", nickname = "checkFilePresence", httpMethod = "GET")
	@RequestMapping(value = "check", method = RequestMethod.GET)
	public @ResponseBody boolean checkFilePresence(@RequestParam(value = "filename") String filename)
	{
		return new File(String.format(DATA_FOLDER, filename)).exists();
	}
}