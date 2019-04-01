package com.barath.app;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.aws.support.S3RemoteFileTemplate;
import org.springframework.integration.aws.support.S3SessionFactory;
import org.springframework.integration.aws.support.filters.S3PersistentAcceptOnceFileListFilter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.metadata.SimpleMetadataStore;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.transformer.StreamTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.scheduling.support.PeriodicTrigger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

@Configuration
public class AWSS3Configuration{

	@Value("${aws.accessKeyId}")
	private String accessKey;

	@Value("${aws.secretKey}")
	private String accessSecret;

	@Bean
	public BasicAWSCredentials basicAWSCredentials(){
		BasicAWSCredentials basicAWSCredentials=new BasicAWSCredentials(accessKey,accessSecret);
		return  basicAWSCredentials;
	}
	

	@Bean
	@InboundChannelAdapter(value = "s3Channel", poller = @Poller(fixedDelay = "100"))
	public MessageSource<InputStream> s3InboundStreamingMessageSource() {
		S3StreamingMessageSource messageSource = new S3StreamingMessageSource(template());
		messageSource.setRemoteDirectory("test-barath-s3");
		messageSource.setFilter(new S3PersistentAcceptOnceFileListFilter(new SimpleMetadataStore(),
				"streaming"));
		return messageSource;
	}

	@Bean
	@Transformer(inputChannel = "s3Channel", outputChannel = "outputChannel")
	public org.springframework.integration.transformer.Transformer transformer() {
		return new StreamTransformer();
	}

	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata defaultPoller() {

		PollerMetadata pollerMetadata = new PollerMetadata();
		pollerMetadata.setTrigger(new PeriodicTrigger(10));
		return pollerMetadata;
	}

	@Bean
	public S3RemoteFileTemplate template() {
		return new S3RemoteFileTemplate(new S3SessionFactory(s3client()));
	}

	@Bean
	public AmazonS3 s3client() {
		return new AmazonS3Client(basicAWSCredentials());
	}

	@Bean
	public PollableChannel s3Channel() {
		return new QueueChannel();
	}

	@Bean
	public MessageChannel outputChannel(){
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow s3Flow(){

		return IntegrationFlows.from(outputChannel())
				.handle( (message) -> {
					System.out.println("Message received "+message);
				}).get();
	}

}