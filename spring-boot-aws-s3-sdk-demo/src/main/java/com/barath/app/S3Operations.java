package com.barath.app;

import java.io.File;
import java.util.List;

import com.amazonaws.services.s3.model.Bucket;

public interface S3Operations {
	
	boolean postObject(String bucketName,File file);
	
	boolean deleteObject(String bucketName,File file);
	
	List<String> getObjects(String bucketName);
	
	List<Bucket> listBuckets();
	

}
