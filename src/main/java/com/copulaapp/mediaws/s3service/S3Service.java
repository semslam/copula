package com.copulaapp.mediaws.s3service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.net.URL;

/**
 * Created by heeleaz on 7/17/17.
 */
public class S3Service {
    private static final String ACCESS_KEY = "AKIAJS6H35EIJJP52NEQ";
    private static final String SECRET_ACCESS = "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";

    private AmazonS3 s3Client;

    S3Service(String accessKey, String secretAccess) {
        //new ProfileCredentialsProvider().getCredentials();
        AWSCredentials credentials =
                new BasicAWSCredentials(accessKey, secretAccess);
        s3Client = new AmazonS3Client(credentials);
    }

    public S3Service() {
        this(ACCESS_KEY, SECRET_ACCESS);
    }

    public AmazonS3 getS3Client() {
        return s3Client;
    }

    public URL getPublicUrl(String bucketName, String key) {
        return s3Client.getUrl(bucketName, key);
    }
}
