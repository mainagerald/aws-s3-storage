package com.example.org.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;


@Configuration
public class AwsStorageConfig {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretAccessKey;

    @Bean
    public S3Client generateS3Client(){
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretAccessKey);
        return S3Client.builder().region(Region.US_EAST_1).credentialsProvider(()->credentials).build();

    }

}
