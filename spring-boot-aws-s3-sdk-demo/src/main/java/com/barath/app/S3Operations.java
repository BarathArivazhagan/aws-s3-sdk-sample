package com.barath.app;

import java.io.File;
import java.util.List;


public interface S3Operations {
	
	Object postObject(String bucketName,File file);
	
	void deleteObject(String bucketName, String key);
	
	List<String> getObjects(String bucketName);
	
	Object getObject(String bucketName,String key);
	
	List<String> listBuckets();

	String generatePresignedUrl(String bucketName, String key);
	

}
