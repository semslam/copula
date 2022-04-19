package com.copulaapp.mediaws;

import com.copulaapp.mediaws.s3service.S3PutService;
import com.copulaapp.mediaws.s3service.S3Service;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by heeleaz on 7/17/17.
 */
public class ProfileMediaManager {
    private static final String BUCKET_NAME = "copulaapp";
    private static final String FOLDER = "profile_images";

    private static String getFilename(String userId) {
        return FOLDER + "/" + userId + ".jpg";
    }

    public static boolean putImage(String userId, InputStream inputStream) {
        S3PutService putService = new S3PutService();
        putService.setContentType("image/jpeg");
        putService.setFileName(getFilename(userId));
        putService.setStream(inputStream);

        return putService.putObject(BUCKET_NAME);
    }

    public static String getImage(String userId) {
        S3Service s3Service = new S3Service();
        URL url = s3Service.getPublicUrl(BUCKET_NAME, getFilename(userId));
        return (url != null) ? url.toString() : null;
    }
}
