package com.barath.app;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

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
	public Object postObject(String bucketName, File file) {
		
	  PutObjectResult result = this.amazonS3.putObject(bucketName, file.getName(), file);
	  logger.info("Put object result for file {} with version {}",file.getName(), Objects.toString(result.getVersionId()));
	  return result;
	}
	
	public Object postObject(File file) {
		return postObject(bucketName, file);		
	}

	@Override
	public List<String> listBuckets() {
		
		logger.info("Listing bucket names ");
		return this.amazonS3.listBuckets()
				.stream()
				.map(Bucket::getName).collect(Collectors.toList());
	}

	@Override
	public void deleteObject(String bucketName, String key) {
		
		logger.info("Deleteing the object with bucketname {} and key {}",bucketName,key);
		this.amazonS3.deleteObject(bucketName, key);
	}

	public void deleteObject(String key) {
		
		logger.info("Deleteing the object with bucketname {} and key {}",bucketName,key);
		this.amazonS3.deleteObject(bucketName, key);
	}

	@Override
	public List<String> getObjects(String bucketName) {
		
		List<String> results = new ArrayList<>();
		ObjectListing lists= this.amazonS3.listObjects(bucketName);
		lists.getObjectSummaries()
			.stream()
			.map(S3ObjectSummary::getKey)
			.forEach(results::add);
		while(lists.isTruncated()){
			ObjectListing nextLists=  this.amazonS3.listNextBatchOfObjects(lists);				
			nextLists.getObjectSummaries()
			.stream()
			.map(S3ObjectSummary::getKey)
			.forEach(results::add);
		}
		
		return results;
	}
	

	public List<String> getObjects() {		
		return this.getObjects(bucketName);
	}

	@Override
	public Object getObject(String bucketName, String key) {
		
		logger.info("Get object from bucket {} with key {}",bucketName,key);
		S3Object s3Object=	this.amazonS3.getObject(bucketName, key);
		logger.info("Got object ",s3Object.getKey());
		return s3Object;
	}
	

	public Object getObject(String key) {
		return this.getObject(bucketName, key);
	}
	
	

}
