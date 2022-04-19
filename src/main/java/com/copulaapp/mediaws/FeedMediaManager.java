package com.copulaapp.mediaws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.copulaapp.mediaws.s3service.S3PutService;
import com.copulaapp.mediaws.s3service.S3Service;
import com.copulaapp.webservice.models.feed.FeedEntry;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by heeleaz on 7/17/17.
 */
public class FeedMediaManager {
    private static final String BUCKET_NAME = "copulaapp";
    private static final String VIDEO_FOLDER = "feeds/videos";
    private static final String IMAGE_FOLDER = "feeds/images";

    private static String getFile(String folder, String key) {
        S3Service s3Service = new S3Service();
        URL url = s3Service.getPublicUrl(BUCKET_NAME, ext(folder, key));
        return (url != null) ? url.toString() : null;
    }

    public static String getVideoThumbnail(String feedId) {
        return getFile(VIDEO_FOLDER, feedId + ".jpg");
    }

    public static String getVideo(String feedId) {
        return getFile(VIDEO_FOLDER, feedId + ".mp4");
    }

    public static String getImage(String feedId) {
        return getFile(IMAGE_FOLDER, feedId + ".jpg");
    }

    private static String ext(String folder, String key) {
        return folder + "/" + key;
    }

    public static boolean putImage(String feedId, InputStream is, String type) {
        S3PutService putService = new S3PutService();
        putService.setContentType(type);
        putService.setFileName(IMAGE_FOLDER + "/" + feedId + ".jpg");
        putService.setStream(is);

        return putService.putObject(BUCKET_NAME);
    }

    public static boolean putVideoThumbnail(String feedId, InputStream is) {
        S3PutService putService = new S3PutService();
        putService.setContentType("image/jpeg");
        putService.setStream(is);
        putService.setFileName(VIDEO_FOLDER + "/" + feedId + ".jpg");

        return putService.putObject(BUCKET_NAME);
    }

    public static boolean putVideo(String feedId, InputStream is) {
        try {
            S3PutService putService = new S3PutService();
            putService.setContentType("video/mp4");
            putService.setStream(is);
            putService.setFileName(ext(VIDEO_FOLDER, feedId + ".mp4"));
            return putService.putObject(BUCKET_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void rename(String uploadMediaId, String feedId, int mediaType) {
        if (mediaType == FeedEntry.IMAGE)
            renameImage(uploadMediaId, feedId);
        else if (mediaType == FeedEntry.VIDEO)
            renameVideo(uploadMediaId, feedId);
    }

    public static void renameImage(String source, String target) {
        source = ext(IMAGE_FOLDER, source + ".jpg");
        target = ext(IMAGE_FOLDER, target + ".jpg");

        renameFile(source, target);
    }

    public static void renameVideo(String source, String target) {
        String thumbSource = ext(VIDEO_FOLDER, source + ".jpg");
        String thumbTarget = ext(VIDEO_FOLDER, target + ".jpg");
        renameFile(thumbSource, thumbTarget);

        String fileSource = ext(VIDEO_FOLDER, source + ".mp4");
        String fileTarget = ext(VIDEO_FOLDER, target + ".mp4");
        renameFile(fileSource, fileTarget);
    }

    public static void renameFile(String oldName, String newName) {
        AmazonS3 s3Client = new S3Service().getS3Client();
        CopyObjectRequest copyObjRequest = new CopyObjectRequest(BUCKET_NAME,
                oldName, BUCKET_NAME, newName).
                withCannedAccessControlList(CannedAccessControlList.PublicRead);
        s3Client.copyObject(copyObjRequest);
        s3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, oldName));
    }
}
