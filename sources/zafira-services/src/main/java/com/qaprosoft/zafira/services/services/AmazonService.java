package com.qaprosoft.zafira.services.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Service
public class AmazonService
{
	public static final String COMMENT_KEY = "comment";
	
	@Autowired
	private AmazonS3 s3Client;

	@Value("${zafira.amazon.bucket}")
	private String s3Bucket;

	public List<S3ObjectSummary> listFiles(String filePrefix)
	{
		ListObjectsRequest listObjectRequest = new ListObjectsRequest().withBucketName(s3Bucket).withPrefix(filePrefix);
		return s3Client.listObjects(listObjectRequest).getObjectSummaries();
	}
	
	public String getComment(String key)
	{
		return s3Client.getObjectMetadata(s3Bucket, key).getUserMetaDataOf(COMMENT_KEY);
	}
	
	public String getPublicLink(S3ObjectSummary objectSummary)
	{
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(s3Bucket,
				objectSummary.getKey());
		generatePresignedUrlRequest.setMethod(HttpMethod.GET);
		return s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
	}
}
