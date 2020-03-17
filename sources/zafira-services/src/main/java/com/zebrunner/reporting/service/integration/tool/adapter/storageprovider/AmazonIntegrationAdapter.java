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
 ******************************************************************************/
package com.zebrunner.reporting.service.integration.tool.adapter.storageprovider;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.SdkBufferedInputStream;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import com.zebrunner.reporting.domain.dto.aws.FileUploadType;
import com.zebrunner.reporting.domain.dto.aws.SessionCredentials;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.exception.ExternalSystemException;
import com.zebrunner.reporting.service.integration.tool.adapter.AbstractIntegrationAdapter;
import com.zebrunner.reporting.service.integration.tool.adapter.AdapterParam;
import com.zebrunner.reporting.service.util.URLResolver;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

public class AmazonIntegrationAdapter extends AbstractIntegrationAdapter implements StorageProviderAdapter {

    private static final String FILE_PATH_SEPARATOR = "/";

    private final String accessKey;
    private final String secretKey;
    private final String bucket;
    private final String region;

    private final URLResolver urlResolver;
    private final boolean multitenant;
    private final AmazonS3 amazonS3;
    private final AWSSecurityTokenService awsSecurityTokenService;
    private final BasicAWSCredentials basicAWSCredentials;

    public AmazonIntegrationAdapter(Integration integration,
                                    URLResolver urlResolver,
                                    Map<String, Object> additionalProperties) {
        super(integration);

        this.urlResolver = urlResolver;
        this.multitenant = (Boolean) additionalProperties.get("multitenant");
        this.accessKey = getAttributeValue(integration, AmazonParam.AMAZON_ACCESS_KEY);
        this.secretKey = getAttributeValue(integration, AmazonParam.AMAZON_SECRET_KEY);
        this.bucket = getAttributeValue(integration, AmazonParam.AMAZON_BUCKET);
        this.region = getAttributeValue(integration, AmazonParam.AMAZON_REGION);

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setMaxConnections(100);
        clientConfiguration.setProtocol(Protocol.HTTPS);

        this.basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .withRegion(Regions.fromName(region))
                .withClientConfiguration(clientConfiguration).build();
        this.awsSecurityTokenService = AWSSecurityTokenServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .withRegion(Regions.fromName(region))
                .withClientConfiguration(clientConfiguration).build();
    }

    private enum AmazonParam implements AdapterParam {
        AMAZON_ACCESS_KEY("AMAZON_ACCESS_KEY"),
        AMAZON_SECRET_KEY("AMAZON_SECRET_KEY"),
        AMAZON_BUCKET("AMAZON_BUCKET"),
        AMAZON_REGION("AMAZON_REGION");

        private final String name;

        AmazonParam(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return amazonS3.doesBucketExistV2(bucket);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String saveFile(FileUploadType fileType) {
        String result;
        SdkBufferedInputStream stream = null;
        try {
            InputStream inputStream = fileType.getInputStream();
            long originalSize = fileType.getFileSize();
            int size = (int) (originalSize + 100);

            stream = new SdkBufferedInputStream(inputStream, size);
            String type = Mimetypes.getInstance().getMimetype(fileType.getFileName());
            String relativePath = getFileKey(fileType);
            String key = TenancyContext.getTenantName() + relativePath;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(type);
            metadata.setContentLength(originalSize);

            PutObjectRequest putRequest = new PutObjectRequest(bucket, key, stream, metadata);
            amazonS3.putObject(putRequest);
            CannedAccessControlList controlList = multitenant ? CannedAccessControlList.Private : CannedAccessControlList.PublicRead;
            amazonS3.setObjectAcl(bucket, key, controlList);

            CannedAccessControlList acl = multitenant ? CannedAccessControlList.Private : CannedAccessControlList.PublicRead;
            amazonS3.setObjectAcl(bucket, key, acl);

            result = multitenant ? urlResolver.getServiceURL() + relativePath : amazonS3.getUrl(bucket, key).toString();

        } catch (AmazonClientException e) {
            throw new ExternalSystemException("Can't save file to AWS S3", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return result;
    }

    @Override
    public void removeFile(String linkToFile) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, new URL(linkToFile).getPath().substring(1)));
        } catch (MalformedURLException e) {
            throw new ExternalSystemException("Cannot remove file from amazon by link " + linkToFile, e);
        }
    }

    @Override
    public Optional<SessionCredentials> getTemporarySessionCredentials(int expiresIn) {
        SessionCredentials result = null;
        GetSessionTokenRequest getSessionTokenRequest = new GetSessionTokenRequest();
        GetSessionTokenResult getSessionTokenResult;
        getSessionTokenRequest.setDurationSeconds(expiresIn);
        try {
            getSessionTokenResult = awsSecurityTokenService.getSessionToken(getSessionTokenRequest);
            Credentials credentials = getSessionTokenResult.getCredentials();
            result = new SessionCredentials(credentials.getAccessKeyId(), credentials.getSecretAccessKey(),
                    credentials.getSessionToken(), amazonS3.getRegionName(), bucket);
        } catch (Exception e) {
            LOGGER.error("Credentials for Security Token Service are invalid.", e);
        }
        return Optional.ofNullable(result);
    }

    private String getFileKey(final FileUploadType file) {
        return file.getType().getPath() + FILE_PATH_SEPARATOR + RandomStringUtils.randomAlphanumeric(20) + "." +
                FilenameUtils.getExtension(file.getFileName());
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public String getRegion() {
        return region;
    }

    public AmazonS3 getAmazonS3() {
        return amazonS3;
    }

    public AWSSecurityTokenService getAwsSecurityTokenService() {
        return awsSecurityTokenService;
    }

    public BasicAWSCredentials getBasicAWSCredentials() {
        return basicAWSCredentials;
    }
}
