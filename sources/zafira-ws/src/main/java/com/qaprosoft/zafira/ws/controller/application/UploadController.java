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
package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.models.dto.EmailType;
import com.qaprosoft.zafira.services.services.application.EmailService;
import com.qaprosoft.zafira.services.services.application.UploadService;
import com.qaprosoft.zafira.services.services.application.emails.CommonEmail;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.qaprosoft.zafira.models.dto.aws.FileUploadType.Type;

@Api("Upload files API")
@CrossOrigin
@RequestMapping(path = "api/upload", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UploadController extends AbstractController {

    private final EmailService emailService;
    private final UploadService uploadService;

    @Value("${zafira.multitenant}")
    private boolean multitenant;

    public UploadController(EmailService emailService, UploadService uploadService) {
        this.emailService = emailService;
        this.uploadService = uploadService;
    }

    @ApiOperation(value = "Upload file", nickname = "uploadFile", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping()
    public String uploadFile(@RequestHeader("FileType") Type type, @RequestParam("file") MultipartFile file) {
        return uploadService.upload(type, file);
    }

    @ApiOperation(value = "Send image by email", nickname = "sendImageByEmail", httpMethod = "POST")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/email")
    public void sendImageByEmail(@RequestPart("file") MultipartFile file, @RequestPart("email") EmailType email) throws IOException {
        List<Attachment> attachments = new ArrayList<>();
        File attachment = File.createTempFile(
                FilenameUtils.getName(file.getOriginalFilename()),
                "." + FilenameUtils.getExtension(file.getOriginalFilename()));
        file.transferTo(attachment);
        attachments.add(new Attachment(email.getSubject(), attachment));
        String[] recipients = obtainRecipients(email.getRecipients());
        emailService.sendEmail(new CommonEmail(email.getSubject(), email.getText(), attachments), recipients);
    }

    private String[] obtainRecipients(String recipientsLine) {
        return recipientsLine.trim()
                             .replaceAll(",", " ")
                             .replaceAll(";", " ")
                             .split(" ");
    }

}
