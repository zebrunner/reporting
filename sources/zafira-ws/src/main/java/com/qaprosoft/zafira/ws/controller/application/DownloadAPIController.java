package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Api("Download files API")
@CrossOrigin
@RequestMapping("api/download")
@RestController
public class DownloadAPIController extends AbstractController {

    private static final String DATA_FOLDER = "/opt/apk/%s";

    @Autowired
    private ServletContext context;

    @ResponseStatusDetails
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Download file by filename", nickname = "downloadFile", httpMethod = "GET")
    @GetMapping()
    public void downloadFile(HttpServletResponse response, @RequestParam("filename") String filename) throws IOException {
        File file = new File(String.format(DATA_FOLDER, filename));
        String mimeType = context.getMimeType(file.getPath());
        response.setContentType(mimeType == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : mimeType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
        response.setContentLength((int) file.length());
        FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
        response.flushBuffer();
    }

    @ResponseStatusDetails
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Check file is present in file system", nickname = "checkFilePresence", httpMethod = "GET")
    @GetMapping("/check")
    public boolean checkFilePresence(@RequestParam("filename") String filename) {
        return new File(String.format(DATA_FOLDER, filename)).exists();
    }

}