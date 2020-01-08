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
package com.qaprosoft.zafira.web.documented;

import com.qaprosoft.zafira.models.dto.EmailType;
import com.qaprosoft.zafira.models.dto.aws.FileUploadType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api("File utils API")
public interface FileUtilDocumentedController {

    @ApiOperation(
            value = "Uploads file to needed location using file type",
            notes = "Returns url to uploaded file",
            nickname = "uploadFile",
            httpMethod = "POST",
            response = String.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "type", paramType = "header", dataType = "string", required = true, value = "File type(USERS, COMMON, VIDEOS or SCREENSHOTS)"),
            @ApiImplicitParam(name = "file", paramType = "path", dataType = "MultipartFile", required = true, value = "File to upload")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns url to uploaded file", response = String.class)
    })
    String uploadFile(FileUploadType.Type type, MultipartFile file) throws IOException;

    @ApiOperation(
            value = "Sends file by email",
            notes = "Sends file using recipients and info from part named 'email'",
            nickname = "sendImageByEmail",
            httpMethod = "POST"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "email", paramType = "path", dataType = "MultipartFile", required = true, value = "Multipart file part named 'email'"),
            @ApiImplicitParam(name = "file", paramType = "path", dataType = "MultipartFile", required = true, value = "Multipart file part named 'file'")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns success if email was sent")
    })
    void sendImageByEmail(MultipartFile file, EmailType email) throws IOException;

    @ApiOperation(
            value = "Downloads file from public server folder by name",
            notes = "Common usage is apk downloading",
            nickname = "downloadFile",
            httpMethod = "GET"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "filename", paramType = "path", dataType = "string", required = true, value = "Filename to download")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns success if file was downloaded successfully")
    })
    void downloadFile(HttpServletResponse response, String filename) throws IOException;

    @ApiOperation(
            value = "Checks that file is present in public server folder by name",
            notes = "Common usage is apk files existing checking",
            nickname = "checkFilePresence",
            httpMethod = "GET",
            response = Boolean.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "file", paramType = "filename", dataType = "string", required = true, value = "Filename to check")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns true if file exists by filename", response = Boolean.class)
    })
    boolean checkFilePresence(String filename);

}
