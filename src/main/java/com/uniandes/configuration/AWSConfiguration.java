package com.uniandes.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@EnableSqs
@Configuration
public class AWSConfiguration {

    @Value("${aws.access.key}")
    private String accessKey;

    @Value("${aws.secret.access.key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Lazy
    @Bean(name = "amazonSQS", destroyMethod = "shutdown")
    public AmazonSQSAsync amazonSQSClient() {
        BasicAWSCredentials basicCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonSQSAsyncClientBuilder.standard().withRegion(region)
            .withCredentials(new AWSStaticCredentialsProvider(basicCredentials)).build();
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory() {
        SimpleMessageListenerContainerFactory msgListenerContainerFactory = new SimpleMessageListenerContainerFactory();
        msgListenerContainerFactory.setAmazonSqs(amazonSQSClient());
        msgListenerContainerFactory.setWaitTimeOut(20);
        msgListenerContainerFactory.setMaxNumberOfMessages(2);
        return msgListenerContainerFactory;
    }

    @Lazy
    @Bean(name = "amazonS3", destroyMethod = "shutdown")
    public AmazonS3 awsS3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard().withRegion(region)
            .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
    }
}
