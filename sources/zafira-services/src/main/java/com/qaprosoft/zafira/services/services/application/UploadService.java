package com.qaprosoft.zafira.services.services.application;

import com.qaprosoft.zafira.models.dto.aws.FileUploadType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.integration.impl.AmazonService;
import com.qaprosoft.zafira.services.util.URLResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UploadService {

    private final static String ASSETS_ROOT = "/opt";
    private final static String ASSETS_DIRECTORY = "/assets/";
    private final static String ASSETS_LOCATION = ASSETS_ROOT + ASSETS_DIRECTORY;

    private final AmazonService amazonService;
    private final URLResolver urlResolver;

    @Value("${zafira.multitenant}")
    private boolean multitenant;


    public UploadService(AmazonService amazonService, URLResolver urlResolver) {
        this.amazonService = amazonService;
        this.urlResolver = urlResolver;
    }

    /**
     * Uploads given document to underlying storage. Exact storage type currently depends on external app configuration
     * and is not controller by service client
     * @param type document type
     * @param file document to be uploaded
     * @return url single-attribute JSON containing document url to be returned to REST API client invoking this API
     */
    public String upload(FileUploadType.Type type, MultipartFile file) {
        String resourceUrl;

        // this is only required for single host deployment and only for users/common assets such as avatar and company logo
        // the rest of the artifacts still should be uploaded to S3 bucket
        if (!multitenant && (FileUploadType.Type.USERS.equals(type) || FileUploadType.Type.COMMON.equals(type))) {
            String filename = storeToLocalFilesystem(file);
            // resource url is a concatenation or API root endpoint and relative path to document
            resourceUrl = urlResolver.buildWebserviceUrl() + ASSETS_DIRECTORY + filename;
        } else {
            resourceUrl = amazonService.saveFile(new FileUploadType(file, type));
        }

        return String.format("{\"url\": \"%s\"}", resourceUrl);
    }

    private String storeToLocalFilesystem(MultipartFile file) {
        String filename = file.getOriginalFilename();
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(ASSETS_LOCATION + filename);
            Files.write(path, bytes);
            return filename;
        } catch (IOException e) {
            throw new ServiceException("Unable to upload document");
        }
    }

}
