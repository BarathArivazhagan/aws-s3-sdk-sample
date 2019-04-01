package com.barath.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AWSS3Configuration {
	
	@Value("${aws.region:us-east-1}")
	private String region;
	
		
	@Bean
	public AWSCredentials awsCredentials(@Value("{aws.accessKey}") String accessKey, @Value("{aws.secretKey}") String secretKey) {
		return new BasicAWSCredentials(accessKey, secretKey);
	}
	
	@Bean
	public AmazonS3 s3(AWSCredentials awsCredentials) {
		
		return AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.withRegion(region)
				.build();
	}

}
