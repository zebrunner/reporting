/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
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
package com.qaprosoft.zafira.services.services.application.jmx.amazon;

import static com.qaprosoft.zafira.models.db.Setting.Tool.AMAZON;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.services.services.application.jmx.CryptoService;
import com.qaprosoft.zafira.services.services.application.jmx.IJMXService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.internal.SdkBufferedInputStream;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.dto.aws.FileUploadType;
import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import com.qaprosoft.zafira.services.exceptions.AWSException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.jmx.context.AmazonContext;

@ManagedResource(objectName = "bean:name=amazonService", description = "Amazon init Managed Bean", currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200)
public class AmazonService implements IJMXService<AmazonContext>, IURLGenerator {

    private static final Logger LOGGER = Logger.getLogger(AmazonService.class);

    public static final String COMMENT_KEY = "comment";

    private static final String FILE_PATH_SEPARATOR = "/";

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private ClientConfiguration clientConfiguration;

    @Override
    public void init() {
        String accessKey = null;
        String privateKey = null;
        String region = null;
        String bucket = null;

        try {
            List<Setting> amazonSettings = settingsService.getSettingsByTool(AMAZON);
            for (Setting setting : amazonSettings) {
                if (setting.isEncrypted()) {
                    setting.setValue(cryptoService.decrypt(setting.getValue()));
                }
                switch (Setting.SettingType.valueOf(setting.getName())) {
                case AMAZON_ACCESS_KEY:
                    accessKey = setting.getValue();
                    break;
                case AMAZON_SECRET_KEY:
                    privateKey = setting.getValue();
                    break;
                case AMAZON_REGION:
                    region = setting.getValue();
                    break;
                case AMAZON_BUCKET:
                    bucket = setting.getValue();
                    break;
                default:
                    break;
                }
            }
            init(accessKey, privateKey, region, bucket);
        } catch (Exception e) {
            LOGGER.error("Setting does not exist", e);
        }
    }

    @ManagedOperation(description = "Amazon initialization")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "accessKey", description = "Amazon access key"),
            @ManagedOperationParameter(name = "privateKey", description = "Amazon private key"),
            @ManagedOperationParameter(name = "region", description = "Amazon region"),
            @ManagedOperationParameter(name = "bucket", description = "Amazon bucket")})
    public void init(String accessKey, String privateKey, String region, String bucket) {
        try {
            if (!StringUtils.isBlank(accessKey) && !StringUtils.isBlank(privateKey) && !StringUtils.isBlank(region)
                    && !StringUtils.isBlank(bucket)) {
                putContext(AMAZON, new AmazonContext(accessKey, privateKey, region, bucket, clientConfiguration));
            }
        } catch (Exception e) {
            LOGGER.error("Unable to initialize Amazon integration: " + e.getMessage());
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return getAmazonType().getAmazonS3().doesBucketExistV2(getAmazonType().getS3Bucket());
        } catch (Exception e) {
            return false;
        }
    }

    public List<S3ObjectSummary> listFiles(String filePrefix) {
        ListObjectsRequest listObjectRequest = new ListObjectsRequest().withBucketName(getAmazonType().getS3Bucket())
                .withPrefix(filePrefix);
        return getAmazonType().getAmazonS3().listObjects(listObjectRequest).getObjectSummaries();
    }

    public String getComment(String key) {
        return getAmazonType().getAmazonS3().getObjectMetadata(getAmazonType().getS3Bucket(), key)
                .getUserMetaDataOf(COMMENT_KEY);
    }

    public String getPublicLink(S3ObjectSummary objectSummary) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
                getAmazonType().getS3Bucket(),
                objectSummary.getKey());
        generatePresignedUrlRequest.setMethod(HttpMethod.GET);
        return getAmazonType().getAmazonS3().generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    public String saveFile(final FileUploadType file) throws ServiceException {
        SdkBufferedInputStream stream = null;
        GeneratePresignedUrlRequest request;
        try {
            stream = new SdkBufferedInputStream(file.getFile().getInputStream(),
                    (int) (file.getFile().getSize() + 100));
            String type = Mimetypes.getInstance().getMimetype(file.getFile().getOriginalFilename());
            String key = getFileKey(file);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(type);
            metadata.setContentLength(file.getFile().getSize());

            PutObjectRequest putRequest = new PutObjectRequest(getAmazonType().getS3Bucket(), key, stream, metadata);
            getAmazonType().getAmazonS3().putObject(putRequest);
            getAmazonType().getAmazonS3().setObjectAcl(getAmazonType().getS3Bucket(), key,
                    CannedAccessControlList.PublicRead);

            request = new GeneratePresignedUrlRequest(getAmazonType().getS3Bucket(), key);

        } catch (IOException e) {
            throw new AWSException("Can't save file to Amazone", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return getAmazonType().getAmazonS3().generatePresignedUrl(request).toString().split("\\?")[0];
    }

    public void removeFile(final String linkToFile) throws ServiceException {
        try {
            getAmazonType().getAmazonS3().deleteObject(
                    new DeleteObjectRequest(getAmazonType().getS3Bucket(), new URL(linkToFile).getPath().substring(1)));
        } catch (MalformedURLException e) {
            throw new ServiceException(e);
        }
    }

    private String getFileKey(final FileUploadType file) {
        return buildPath(file.getType()) + FILE_PATH_SEPARATOR + RandomStringUtils.randomAlphanumeric(20) + "." +
                FilenameUtils.getExtension(file.getFile().getOriginalFilename());
    }

    private String buildPath(FileUploadType.Type type) {
        return TenancyContext.getTenantName() + type.getPath();
    }

    /**
     * Generates temporary credentials for external clients
     * 
     * @return {@link SessionCredentials} object
     */
    public SessionCredentials getTemporarySessionCredentials(int expiresIn) {
        SessionCredentials result = null;
        if (getAmazonType().getAwsSecurityTokenService() != null) {
            GetSessionTokenRequest getSessionTokenRequest = new GetSessionTokenRequest();
            GetSessionTokenResult getSessionTokenResult;
            getSessionTokenRequest.setDurationSeconds(expiresIn);
            try {
                getSessionTokenResult = getAmazonType().getAwsSecurityTokenService()
                        .getSessionToken(getSessionTokenRequest);
                Credentials credentials = getSessionTokenResult.getCredentials();
                result = new SessionCredentials(credentials.getAccessKeyId(), credentials.getSecretAccessKey(),
                        credentials.getSessionToken(), getAmazonType().getAmazonS3().getRegionName(),
                        getAmazonType().getS3Bucket());
            } catch (Exception e) {
                LOGGER.error("Credentials for Security Token Service are invalid.", e);
            }
        }
        return result;
    }

    @Override
    public String generatePresignedURL(Integer expiresIn, String key) {
        final Date expectedDate = DateUtils.addSeconds(new Date(), expiresIn);
        final Date expirationDate = expectedDate.after(DateUtils.addDays(new Date(), 7)) ? DateUtils.addDays(new Date(), 7) : expectedDate;
        return getAmazonType().getAmazonS3().generatePresignedUrl(
                new GeneratePresignedUrlRequest(getAmazonType().getS3Bucket(), key).withExpiration(expirationDate)).toString();
    }

    @ManagedAttribute(description = "Get current amazon entity")
    public AmazonContext getAmazonType() {
        return getContext(AMAZON);
    }
}
