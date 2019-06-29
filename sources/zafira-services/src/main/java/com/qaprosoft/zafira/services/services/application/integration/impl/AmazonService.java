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
package com.qaprosoft.zafira.services.services.application.integration.impl;

import static com.qaprosoft.zafira.models.db.Setting.Tool.AMAZON;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.services.services.application.integration.AbstractIntegration;
import com.qaprosoft.zafira.services.util.URLResolver;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.internal.SdkBufferedInputStream;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import com.qaprosoft.zafira.models.dto.aws.FileUploadType;
import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import com.qaprosoft.zafira.services.exceptions.AWSException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.context.AmazonContext;
import org.springframework.stereotype.Component;

@Component
public class AmazonService extends AbstractIntegration<AmazonContext> {

    private static final String FILE_PATH_SEPARATOR = "/";

    private final URLResolver urlResolver;

    @Value("${zafira.multitenant}")
    private Boolean multitenant;

    public AmazonService(SettingsService settingsService, CryptoService cryptoService, URLResolver urlResolver) {
        super(settingsService, cryptoService, AMAZON, AmazonContext.class);
        this.urlResolver = urlResolver;
    }

    @Override
    public boolean isConnected() {
        try {
            return context().getAmazonS3().doesBucketExistV2(context().getS3Bucket());
        } catch (Exception e) {
            return false;
        }
    }

    public String saveFile(final FileUploadType file) {
        String result;
        SdkBufferedInputStream stream = null;
        try {
            stream = new SdkBufferedInputStream(file.getFile().getInputStream(),
                    (int) (file.getFile().getSize() + 100));
            String type = Mimetypes.getInstance().getMimetype(file.getFile().getOriginalFilename());
            String relativePath = getFileKey(file);
            String key = TenancyContext.getTenantName() + relativePath;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(type);
            metadata.setContentLength(file.getFile().getSize());

            PutObjectRequest putRequest = new PutObjectRequest(context().getS3Bucket(), key, stream, metadata);
            context().getAmazonS3().putObject(putRequest);
            CannedAccessControlList controlList = multitenant ? CannedAccessControlList.Private : CannedAccessControlList.PublicRead;
            context().getAmazonS3().setObjectAcl(context().getS3Bucket(), key, controlList);

            result = multitenant ? urlResolver.getServiceURL() + relativePath
                    : context().getAmazonS3().getUrl(context().getS3Bucket(), key).toString();

        } catch (IOException e) {
            throw new AWSException("Can't save file to Amazone", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return result;
    }

    public void removeFile(final String linkToFile) {
        try {
            context().getAmazonS3().deleteObject(
                    new DeleteObjectRequest(context().getS3Bucket(), new URL(linkToFile).getPath().substring(1)));
        } catch (MalformedURLException e) {
            throw new ServiceException(e);
        }
    }

    private String getFileKey(final FileUploadType file) {
        return file.getType().getPath() + FILE_PATH_SEPARATOR + RandomStringUtils.randomAlphanumeric(20) + "." +
                FilenameUtils.getExtension(file.getFile().getOriginalFilename());
    }

    /**
     * Generates temporary credentials for external clients
     * 
     * @return {@link SessionCredentials} object
     */
    public Optional<SessionCredentials> getTemporarySessionCredentials(int expiresIn) {
        if (!isEnabledAndConnected()) {
            return Optional.empty();
        }
        SessionCredentials result = null;
        GetSessionTokenRequest getSessionTokenRequest = new GetSessionTokenRequest();
        GetSessionTokenResult getSessionTokenResult;
        getSessionTokenRequest.setDurationSeconds(expiresIn);
        try {
            getSessionTokenResult = context().getAwsSecurityTokenService()
                    .getSessionToken(getSessionTokenRequest);
            Credentials credentials = getSessionTokenResult.getCredentials();
            result = new SessionCredentials(credentials.getAccessKeyId(), credentials.getSecretAccessKey(),
                    credentials.getSessionToken(), context().getAmazonS3().getRegionName(),
                    context().getS3Bucket());
        } catch (Exception e) {
            LOGGER.error("Credentials for Security Token Service are invalid.", e);
        }
        return Optional.ofNullable(result);
    }

}
