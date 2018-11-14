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

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.dto.EmailType;
import com.qaprosoft.zafira.models.dto.aws.FileUploadType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.EmailService;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.emails.CommonEmail;
import com.qaprosoft.zafira.services.services.application.jmx.amazon.AmazonService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.qaprosoft.zafira.models.dto.aws.FileUploadType.Type;

@Api(value = "Upload files API")
@Controller
@CrossOrigin
@RequestMapping("api/upload")
public class UploadController extends AbstractController
{

	@Autowired
	private AmazonService amazonService;

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private EmailService emailService;

	@ApiOperation(value = "Upload file", nickname = "uploadFile", httpMethod = "POST", response = String.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String uploadFile(@RequestHeader(value = "FileType") Type type,
			@RequestParam(value = "file") MultipartFile file) throws ServiceException
	{
		return String.format("{\"url\": \"%s\"}", amazonService.saveFile(new FileUploadType(file, type)));
	}

	@ApiOperation(value = "Upload setting file", nickname = "uploadSettingFile", httpMethod = "POST", response = String.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
	@RequestMapping(value = "setting/{tool}/{settingName}", method = RequestMethod.POST)
	public void uploadSettingFile(@RequestParam(value = "file") MultipartFile file, @PathVariable(value = "tool") Setting.Tool tool,
								  @PathVariable(value = "settingName") String settingName) throws Exception {
		settingsService.createSettingFile(file.getBytes(), file.getOriginalFilename(), tool, settingName);
	}

	@ApiOperation(value = "Send image by email", nickname = "sendImageByEmail", httpMethod = "POST")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
	@RequestMapping(value = "email", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void sendImageByEmail(@RequestParam(value = "file") MultipartFile file, @ModelAttribute EmailType email) throws ServiceException, IOException {
		List<Attachment> attachments = new ArrayList<>();
		File attachment = File.createTempFile(FilenameUtils.getName(file.getOriginalFilename()), "." + FilenameUtils.getExtension(file.getOriginalFilename()));
		file.transferTo(attachment);
		attachments.add(new Attachment(email.getSubject(), attachment));
		emailService.sendEmail(new CommonEmail(email.getSubject(), email.getText(), attachments), email.getRecipients().trim().replaceAll(",", " ").replaceAll(";", " ").split(" "));
	}
}
