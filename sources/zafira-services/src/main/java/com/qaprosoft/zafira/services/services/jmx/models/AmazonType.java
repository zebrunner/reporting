package com.qaprosoft.zafira.services.services.jmx.models;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;

public class AmazonType extends AbstractType
{

    private AmazonS3 amazonS3;
    private AWSSecurityTokenService awsSecurityTokenService;
    private BasicAWSCredentials basicAWSCredentials;
    private String s3Bucket;

    public AmazonType(String accessKey, String privateKey, String region, String s3Bucket, ClientConfiguration clientConfiguration)
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
}
