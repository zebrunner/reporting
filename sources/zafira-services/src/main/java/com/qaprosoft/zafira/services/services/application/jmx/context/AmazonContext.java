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
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.jmx.context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;

import java.io.File;
import java.net.URL;

public class AmazonContext extends AbstractContext
{

    private AmazonS3 amazonS3;
    private AWSSecurityTokenService awsSecurityTokenService;
    private BasicAWSCredentials basicAWSCredentials;
    private String s3Bucket;
    private String distributionDomain;
    private String keyPairId;
    private File privateKeyFile;

    public AmazonContext(String accessKey, String privateKey, String region, String s3Bucket, ClientConfiguration clientConfiguration,
                         String distributionDomain, String keyPairId)
    {
        this.s3Bucket = s3Bucket;
        this.basicAWSCredentials = new BasicAWSCredentials(accessKey, privateKey);
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .withRegion(Regions.fromName(region))
                .withClientConfiguration(clientConfiguration).build();
        this.awsSecurityTokenService = AWSSecurityTokenServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .withRegion(Regions.fromName(region))
                .withClientConfiguration(clientConfiguration).build();
        this.distributionDomain = distributionDomain;
        this.keyPairId = keyPairId;
        final URL privateKeyResource = getClass().getClassLoader().getResource(this.keyPairId + ".der");
        this.privateKeyFile = privateKeyResource != null ? new File(privateKeyResource.getFile()) : null;
    }

    public AmazonContext(String accessKey, String privateKey, String region, String s3Bucket, ClientConfiguration clientConfiguration)
    {
        this.s3Bucket = s3Bucket;
        this.basicAWSCredentials = new BasicAWSCredentials(accessKey, privateKey);
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .withRegion(Regions.fromName(region))
                .withClientConfiguration(clientConfiguration).build();
        this.awsSecurityTokenService = AWSSecurityTokenServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .withRegion(Regions.fromName(region))
                .withClientConfiguration(clientConfiguration).build();
    }

    public AmazonS3 getAmazonS3()
    {
        return amazonS3;
    }

    public void setAmazonS3(AmazonS3 amazonS3)
    {
        this.amazonS3 = amazonS3;
    }

    public AWSSecurityTokenService getAwsSecurityTokenService()
    {
        return awsSecurityTokenService;
    }

    public void setAwsSecurityTokenService(AWSSecurityTokenService awsSecurityTokenService)
    {
        this.awsSecurityTokenService = awsSecurityTokenService;
    }

    public BasicAWSCredentials getBasicAWSCredentials()
    {
        return basicAWSCredentials;
    }

    public void setBasicAWSCredentials(BasicAWSCredentials basicAWSCredentials)
    {
        this.basicAWSCredentials = basicAWSCredentials;
    }

    public String getS3Bucket()
    {
        return s3Bucket;
    }

    public void setS3Bucket(String s3Bucket)
    {
        this.s3Bucket = s3Bucket;
    }

    public String getDistributionDomain() {
        return distributionDomain;
    }

    public void setDistributionDomain(String distributionDomain) {
        this.distributionDomain = distributionDomain;
    }

    public String getKeyPairId() {
        return keyPairId;
    }

    public void setKeyPairId(String keyPairId) {
        this.keyPairId = keyPairId;
    }

    public File getPrivateKeyFile() {
        return privateKeyFile;
    }

    public void setPrivateKeyFile(File privateKeyFile) {
        this.privateKeyFile = privateKeyFile;
    }
}
