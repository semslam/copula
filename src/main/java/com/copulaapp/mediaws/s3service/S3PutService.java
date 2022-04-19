package com.copulaapp.mediaws.s3service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.InputStream;

/**
 * Created by heeleaz on 7/17/17.
 */
public class S3PutService {
    private ObjectMetadata metadata = new ObjectMetadata();
    private String fileName;
    private InputStream inputStream;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(long fileSize) {
        metadata.setContentLength(fileSize);
    }

    public void setContentType(String contentType) {
        metadata.setContentType(contentType);
    }

    public void setStream(InputStream is) {
        this.inputStream = is;
    }

    public boolean putObject(String bucketName) {
        AmazonS3 s3Service = new S3Service().getS3Client();
        PutObjectResult result = s3Service.putObject(
                new PutObjectRequest(bucketName, fileName, inputStream, metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
        return (result != null);
    }
}
