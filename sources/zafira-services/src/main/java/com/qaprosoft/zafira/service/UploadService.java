package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.models.dto.aws.FileUploadType;
import com.qaprosoft.zafira.service.exception.ProcessingException;
import com.qaprosoft.zafira.service.integration.tool.impl.StorageProviderService;
import com.qaprosoft.zafira.service.util.URLResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.qaprosoft.zafira.service.exception.ProcessingException.ProcessingErrorDetail.UNPROCESSABLE_DOCUMENT;

@Service
public class UploadService {

    private final static String ASSETS_ROOT = "/opt";
    private final static String ASSETS_DIRECTORY = "/assets/";
    private final static String ASSETS_LOCATION = ASSETS_ROOT + ASSETS_DIRECTORY;

    private final StorageProviderService storageProviderService;
    private final URLResolver urlResolver;

    @Value("${zafira.multitenant}")
    private boolean multitenant;


    public UploadService(StorageProviderService storageProviderService, URLResolver urlResolver) {
        this.storageProviderService = storageProviderService;
        this.urlResolver = urlResolver;
    }

    /**
     * Uploads given document to underlying storage. Exact storage type currently depends on external app configuration
     * and is not controller by service client
     * @param type document type
     * @return url single-attribute JSON containing document url to be returned to REST API client invoking this API
     */
    public String upload(FileUploadType.Type type, InputStream inputStream, String filename, long fileSize) {
        String resourceUrl;

        // this is only required for single host deployment and only for users/common assets such as avatar and company logo
        // the rest of the artifacts still should be uploaded to S3 bucket
        if (!multitenant && (FileUploadType.Type.USERS.equals(type) || FileUploadType.Type.COMMON.equals(type))) {
            filename = storeToLocalFilesystem(filename, inputStream);
            // resource url is a concatenation or API root endpoint and relative path to document
            resourceUrl = urlResolver.buildWebserviceUrl() + ASSETS_DIRECTORY + filename;
        } else {
            resourceUrl = storageProviderService.saveFile(new FileUploadType(inputStream, type, filename, fileSize));
        }

        return String.format("{\"url\": \"%s\"}", resourceUrl);
    }

    private String storeToLocalFilesystem(String filename, InputStream fileStream) {
        try {
            byte[] buffer = new byte[fileStream.available()];
            fileStream.read(buffer);
            Path path = Paths.get(ASSETS_LOCATION + filename);
            Files.write(path, buffer);
            return filename;
        } catch (IOException e) {
            // TODO by nsidorevich on 2019-09-03: review error code, message and exception type
            throw new ProcessingException(UNPROCESSABLE_DOCUMENT, "Unable to upload document");
        }
    }

}
