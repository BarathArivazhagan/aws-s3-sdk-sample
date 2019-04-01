package com.barath.app;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectResult;

@Service
public class AWSS3Service implements S3Operations {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Value("${aws.bucketName}")
	private String bucketName;
	
	private final AmazonS3 amazonS3;	
	

	public AWSS3Service(AmazonS3 amazonS3) {
		super();		
		this.amazonS3 = amazonS3;
	}

	@Override
	public boolean postObject(String bucketName, File file) {
		
	  PutObjectResult result =amazonS3.putObject(bucketName, file.getName(), file);
	  logger.info("put object result {}",Objects.toString(result));
	  return result.getVersionId() !=null ? true: false;
	}
	
	public boolean postObject(File file) {
		return postObject(bucketName, file);		
	}

	@Override
	public List<Bucket> listBuckets() {
		
		logger.info("listing bucket names ");
		return this.amazonS3.listBuckets();
	}
	
	

}
