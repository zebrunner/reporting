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
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.models.dto.EmailType;
import com.qaprosoft.zafira.service.EmailService;
import com.qaprosoft.zafira.service.UploadService;
import com.qaprosoft.zafira.web.documented.FileUtilDocumentedController;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import static com.qaprosoft.zafira.models.dto.aws.FileUploadType.Type;

@CrossOrigin
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class FileUtilController extends AbstractController implements FileUtilDocumentedController {

    private static final String DATA_FOLDER = "/opt/apk/%s";

    private final EmailService emailService;
    private final UploadService uploadService;
    private final ServletContext context;

    public FileUtilController(
            EmailService emailService,
            UploadService uploadService,
            ServletContext context
    ) {
        this.emailService = emailService;
        this.uploadService = uploadService;
        this.context = context;
    }

    @PostMapping("api/upload")
    @Override
    public String uploadFile(@RequestHeader("FileType") Type type, @RequestParam("file") MultipartFile file) throws IOException {
        return uploadService.upload(type, file.getInputStream(), file.getOriginalFilename(), file.getSize());
    }

    @PostMapping("api/upload/email")
    @Override
    public void sendImageByEmail(@RequestPart("file") MultipartFile file, @RequestPart("email") EmailType email) throws IOException {
        String fileExtension = String.format(".%s", FilenameUtils.getExtension(file.getOriginalFilename()));
        File attachmentFile = File.createTempFile(UUID.randomUUID().toString(), fileExtension);
        file.transferTo(attachmentFile);
        emailService.sendEmail(email, attachmentFile, file.getResource().getFilename());
    }

    @GetMapping("api/download")
    @Override
    public void downloadFile(HttpServletResponse response, @RequestParam("filename") String filename) throws IOException {
        File file = new File(String.format(DATA_FOLDER, filename));
        String mimeType = context.getMimeType(file.getPath());
        mimeType = mimeType == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : mimeType;
        response.setContentType(mimeType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
        response.setContentLength((int) file.length());
        FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
        response.flushBuffer();
    }

    @GetMapping("api/download/check")
    @Override
    public boolean checkFilePresence(@RequestParam("filename") String filename) {
        return new File(String.format(DATA_FOLDER, filename)).exists();
    }

}
