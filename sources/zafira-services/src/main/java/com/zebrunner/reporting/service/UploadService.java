package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.dto.aws.FileUploadType;
import com.zebrunner.reporting.service.exception.ProcessingException;
import com.zebrunner.reporting.service.integration.tool.impl.StorageProviderService;
import com.zebrunner.reporting.service.util.URLResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.zebrunner.reporting.service.exception.ProcessingException.ProcessingErrorDetail.UNPROCESSABLE_DOCUMENT;

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
     * Uploads artifacts like screenshots or video to to S3 bucket.
     * @param type artifact type
     * @return url single-attribute JSON containing document url to be returned to REST API client invoking this API
     */
    public String uploadArtifacts(FileUploadType.Type type, InputStream inputStream, String filename, long fileSize) {
        String resourceUrl = storageProviderService.saveFile(new FileUploadType(inputStream, type, filename, fileSize));
        return String.format("{\"url\": \"%s\"}", resourceUrl);
    }

    /**
     * Uploads users/common assets such as avatar and company logo to underlying storage.
     * Exact storage type currently depends on external app configuration
     * and is not controlled by service client
     * @return url single-attribute JSON containing document url to be returned to REST API client invoking this API
     */
    public String uploadImages(FileUploadType.Type type, InputStream inputStream, String filename, long fileSize) {
        if (!multitenant) {
            filename = storeToLocalFilesystem(filename, inputStream);
            return getLocalFilesystemResourceURL(filename);
        } else {
            return uploadArtifacts(type, inputStream, filename, fileSize);
        }
    }

    /**
     * Provides resource url via
     * concatenation or API root endpoint and relative path to document
     * @param filename name of the file stored on the local system
     * @return url single-attribute JSON containing document url to be returned to REST API client invoking this API
     */
    private String getLocalFilesystemResourceURL(String filename) {
        String resourceUrl = urlResolver.buildWebserviceUrl() + ASSETS_DIRECTORY + filename;
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
