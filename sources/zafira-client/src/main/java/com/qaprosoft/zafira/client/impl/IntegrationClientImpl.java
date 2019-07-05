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
package com.qaprosoft.zafira.client.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.internal.SdkBufferedInputStream;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.qaprosoft.zafira.client.Path;
import com.qaprosoft.zafira.client.BasicClient;
import com.qaprosoft.zafira.client.IntegrationClient;
import com.qaprosoft.zafira.models.dto.auth.TenantType;
import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import com.qaprosoft.zafira.util.http.HttpClient;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.qaprosoft.zafira.util.AsyncUtil.get;

public class IntegrationClientImpl implements IntegrationClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationClientImpl.class);

    private static final String ERR_MSG_GET_AWS_CREDENTIALS = "Unable to get AWS session credentials";
    private static final String ERR_MSG_GET_GOOGLE_CREDENTIALS = "Unable to get Google session credentials";

    private final BasicClient client;

    private CompletableFuture<AmazonS3> amazonClient;
    private CompletableFuture<Sheets> sheets;
    private SessionCredentials amazonS3SessionCredentials;

    public IntegrationClientImpl(BasicClient client) {
        this.client = client;
    }

    @Override
    public String uploadFile(File file, Integer expiresIn, String keyPrefix) throws Exception {
        String filePath = null;
        TenantType tenantType = client.getTenantType();
        if (getAmazonClient() != null && tenantType != null && !StringUtils.isBlank(tenantType.getTenant())) {
            String fileName = RandomStringUtils.randomAlphanumeric(20) + "." + FilenameUtils.getExtension(file.getName());
            String relativeKey = keyPrefix + fileName;
            String key = tenantType.getTenant() + relativeKey;

            try (SdkBufferedInputStream stream = new SdkBufferedInputStream(new FileInputStream(file), (int) (file.length() + 100))) {
                String type = Mimetypes.getInstance().getMimetype(file.getName());

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(type);
                metadata.setContentLength(file.length());

                PutObjectRequest putRequest = new PutObjectRequest(this.amazonS3SessionCredentials.getBucket(), key, stream, metadata);
                getAmazonClient().putObject(putRequest);
                CannedAccessControlList controlList = tenantType.isUseArtifactsProxy() ? CannedAccessControlList.Private
                        : CannedAccessControlList.PublicRead;
                getAmazonClient().setObjectAcl(this.amazonS3SessionCredentials.getBucket(), key, controlList);

                filePath = tenantType.isUseArtifactsProxy() ? client.getRealServiceUrl() + relativeKey : getFilePath(key);

            } catch (Exception e) {
                LOGGER.error("Can't save file to Amazon S3", e);
            }
        } else {
            throw new Exception("Can't save file to Amazon S3. Verify your credentials or bucket name");
        }

        return filePath;
    }

    private String getFilePath(String key) {
        return getAmazonClient().getUrl(this.amazonS3SessionCredentials.getBucket(), key).toString();
    }

    /**
     * Registers Amazon S3 client
     */
    private CompletableFuture<AmazonS3> initAmazonS3Client() {
        this.amazonClient = CompletableFuture.supplyAsync(() -> {
            this.amazonS3SessionCredentials = getAmazonSessionCredentials().getObject();
            AmazonS3 client = null;
            if (this.amazonS3SessionCredentials != null) {
                try {
                    client = AmazonS3ClientBuilder.standard()
                                                  .withCredentials(
                                                          new AWSStaticCredentialsProvider(new BasicSessionCredentials(this.amazonS3SessionCredentials.getAccessKeyId(),
                                                                  this.amazonS3SessionCredentials.getSecretAccessKey(), this.amazonS3SessionCredentials.getSessionToken())))
                                                  .withRegion(Regions.fromName(this.amazonS3SessionCredentials.getRegion())).build();
                    if (!client.doesBucketExistV2(this.amazonS3SessionCredentials.getBucket())) {
                        throw new Exception(
                                String.format("Amazon S3 bucket with name '%s' doesn't exist.", this.amazonS3SessionCredentials.getBucket()));
                    }
                } catch (Exception e) {
                    LOGGER.error("Amazon integration is invalid. Verify your credentials or region.", e);
                }
            }
            return client;
        });
        return amazonClient;
    }

    @Override
    public Optional<Sheets> getSpreadsheetService() {
        if (!client.isAvailable())
            LOGGER.error("Spreadsheet`s operations are unavailable until connection with Zafira is established!");
        return Optional.ofNullable(getSheets());
    }

    /**
     * Gets Amazon S3 temporary credentials
     *
     * @return Amazon S3 temporary credentials
     */
    private HttpClient.Response<SessionCredentials> getAmazonSessionCredentials() {
        return HttpClient.uri(Path.AMAZON_SESSION_CREDENTIALS_PATH, client.getServiceUrl())
                         .withAuthorization(client.getAuthToken())
                         .onFailure(ERR_MSG_GET_AWS_CREDENTIALS)
                         .get(SessionCredentials.class);
    }

    private CompletableFuture<Sheets> initGoogleClient() {
        this.sheets = CompletableFuture.supplyAsync(() -> {
            Sheets sheets = null;
            String accessToken = getGoogleSessionCredentials().getObject();
            if (accessToken != null) {
                try {
                    GoogleCredential googleCredential = new GoogleCredential().setAccessToken(accessToken);
                    sheets = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), googleCredential)
                            .setApplicationName(UUID.randomUUID().toString())
                            .build();
                } catch (Exception e) {
                    LOGGER.error("Google integration is invalid", e);
                }
            }
            return sheets;
        });
        return sheets;
    }

    /**
     * Gets Google temporary credentials
     *
     * @return Google temporary credentials
     */
    private HttpClient.Response<String> getGoogleSessionCredentials() {
        return HttpClient.uri(Path.GOOGLE_SESSION_CREDENTIALS_PATH, client.getServiceUrl())
                         .withAuthorization(client.getAuthToken())
                         .type(MediaType.TEXT_PLAIN)
                         .accept(MediaType.TEXT_PLAIN)
                         .onFailure(ERR_MSG_GET_GOOGLE_CREDENTIALS)
                         .get(String.class);
    }

    private AmazonS3 getAmazonClient() {
        return get(this.amazonClient, this::initAmazonS3Client);
    }

    private Sheets getSheets() {
        return get(this.sheets, this::initGoogleClient);
    }

}
