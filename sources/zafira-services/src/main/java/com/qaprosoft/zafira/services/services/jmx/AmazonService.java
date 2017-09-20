package com.qaprosoft.zafira.services.services.jmx;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.SettingsService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.qaprosoft.zafira.models.db.Setting.Tool.AMAZON;

@ManagedResource(objectName = "bean:name=amazonService", description = "Amazon init Managed Bean",
		currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200)
public class AmazonService implements IJMXService
{

	private static final Logger LOGGER = Logger.getLogger(AmazonService.class);

	public static final String COMMENT_KEY = "comment";

	private AmazonS3 s3Client;

	private BasicAWSCredentials awsCredentials;

	private String s3Bucket;

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private ClientConfiguration clientConfiguration;

	@Override
	@PostConstruct
	public void init()
	{
		String accessKey = null;
		String privateKey = null;
		String bucket = null;

		try
		{
			List<Setting> jiraSettings = settingsService.getSettingsByTool(AMAZON);
			for (Setting setting : jiraSettings)
			{
				if (setting.isEncrypted())
				{
					setting.setValue(cryptoService.decrypt(setting.getValue()));
				}
				switch (Setting.SettingType.valueOf(setting.getName()))
				{
				case AMAZON_ACCESS_KEY:
					accessKey = setting.getValue();
					break;
				case AMAZON_SECRET_KEY:
					privateKey = setting.getValue();
					break;
				case AMAZON_BUCKET:
					bucket = setting.getValue();
					break;
				default:
					break;
				}
			}
			init(accessKey, privateKey, bucket);
		} catch (Exception e)
		{
			LOGGER.error("Setting does not exist", e);
		}
	}

	@ManagedOperation(description = "Amazon initialization")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "accessKey", description = "Amazon access key"),
			@ManagedOperationParameter(name = "privateKey", description = "Amazon private key"),
			@ManagedOperationParameter(name = "bucket", description = "Amazon bucket") })
	public void init(String accessKey, String privateKey, String bucket)
	{
		try
		{
			if (!StringUtils.isEmpty(accessKey) && !StringUtils.isEmpty(privateKey) && !StringUtils.isEmpty(bucket))
			{
				this.s3Bucket = bucket;
				this.awsCredentials = new BasicAWSCredentials(accessKey, privateKey);
				this.s3Client = new AmazonS3Client(this.awsCredentials, this.clientConfiguration);
			}
		} catch (Exception e)
		{
			LOGGER.error("Unable to initialize Jira integration: " + e.getMessage());
		}
	}

	@Override
	public boolean isConnected()
	{
		try
		{
			this.s3Client.getS3AccountOwner();
			return this.s3Client.doesBucketExist(this.s3Bucket);
		} catch (Exception e)
		{
			return false;
		}
	}

	public List<S3ObjectSummary> listFiles(String filePrefix)
	{
		ListObjectsRequest listObjectRequest = new ListObjectsRequest().withBucketName(s3Bucket).withPrefix(filePrefix);
		return getS3Client().listObjects(listObjectRequest).getObjectSummaries();
	}

	public String getComment(String key)
	{
		return getS3Client().getObjectMetadata(s3Bucket, key).getUserMetaDataOf(COMMENT_KEY);
	}

	public String getPublicLink(S3ObjectSummary objectSummary)
	{
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(s3Bucket,
				objectSummary.getKey());
		generatePresignedUrlRequest.setMethod(HttpMethod.GET);
		return getS3Client().generatePresignedUrl(generatePresignedUrlRequest).toString();
	}

	@ManagedAttribute(description = "Get amazon client")
	public AmazonS3 getS3Client()
	{
		return s3Client;
	}
}
