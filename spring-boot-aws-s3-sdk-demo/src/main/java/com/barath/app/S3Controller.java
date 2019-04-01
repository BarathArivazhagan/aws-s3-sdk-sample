package com.barath.app;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.Bucket;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private final AWSS3Service s3Service; 
	
	
	public S3Controller(AWSS3Service s3Service) {
		super();
		this.s3Service = s3Service;
	}
	
	@PostMapping("/upload")
	public String uploadObject(@RequestBody @Valid @NotNull MultipartFile inputFile) throws IllegalStateException, IOException {
		
		logger.info(" uploading file to S3 with file name {}",inputFile.getOriginalFilename());
		File file = new File( inputFile.getOriginalFilename());
		inputFile.transferTo(file);
		return s3Service.postObject(file) ? "file uploaded to s3 successfully": "upload failed";		

	}
	
	@GetMapping("/buckets")
	public List<String> listBuckets(){
		return this.s3Service.listBuckets()
				.stream()
				.map(Bucket::getName)
				.collect(Collectors.toList());
	}

}
