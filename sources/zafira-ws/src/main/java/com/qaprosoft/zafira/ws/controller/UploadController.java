package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.models.dto.aws.FileUploadType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.jmx.AmazonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.qaprosoft.zafira.models.dto.aws.FileUploadType.Type;

@Api(value = "Upload files API")
@Controller
@CrossOrigin
@RequestMapping("api/upload")
public class UploadController extends AbstractController
{

	@Autowired
	private AmazonService amazonService;

	@ApiOperation(value = "Upload file", nickname = "uploadFile", code = 200, httpMethod = "POST", response = String.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String uploadFile(@RequestHeader(value = "FileType", required = true) Type type, 
			@RequestParam(value = "file", required = true) MultipartFile file) throws ServiceException
	{
		return String.format("{\"url\": \"%s\"}", amazonService.saveFile(new FileUploadType(file, type), getPrincipalId()));
	}
}
