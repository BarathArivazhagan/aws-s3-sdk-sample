package com.barath.app;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3Object;


@RestController
@RequestMapping("/api/s3")
public class S3Controller {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private final AWSS3Service s3Service; 
	
	@Value("${java.io.tmpdir}")
	private String tempLocation;
	
	
	public S3Controller(AWSS3Service s3Service) {
		super();
		this.s3Service = s3Service;
	}
	
	@PostMapping("/upload")
	public String uploadObject(@RequestBody @Valid @NotNull MultipartFile inputFile) throws IllegalStateException, IOException {

		logger.info("Uploading file to S3 with file name {}",inputFile.getOriginalFilename());
		File file = new File(tempLocation.concat(inputFile.getOriginalFilename()));
		
		if(!file.exists()) {
			file.createNewFile();
		}		
		inputFile.transferTo(file);
		return s3Service.postObject(file) !=null ? "File uploaded to s3 successfully": "Upload failed";		

	}
	
	@GetMapping("/buckets")
	public List<String> listBuckets(){
		
		return this.s3Service.listBuckets();
				
	}
	
	@GetMapping("/bucket/{bucketName}/keys")
	public List<String> keysByBucket(@PathVariable String bucketName){
		
		logger.info("Get the list of keys by bucket name {}", bucketName);
		return this.s3Service.getObjects(bucketName);
	}
	
	@GetMapping("/bucket/{bucketName}/key/{keyName}")
	public Object findObjectByKey(@PathVariable @NonNull String bucketName, @PathVariable @NonNull String keyName) throws IOException {
		
		S3Object s3Object= (S3Object) this.s3Service.getObject(bucketName, keyName);
		File file = new File(tempLocation.concat(s3Object.getKey()).concat("_result"));
		Files.copy(s3Object.getObjectContent(),file.toPath());
		return file;
	}
	
	@DeleteMapping("/bucket/{bucketName}/key/{keyName}")
	public void deleteObjectByKey(@PathVariable @NonNull String bucketName, @PathVariable @NonNull String keyName) throws IOException {
		
		this.s3Service.deleteObject(bucketName, keyName);
	}

}
