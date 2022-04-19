package com.copulaapp.mediaws.controllers;

import com.copulaapp.mediaws.FeedMediaManager;
import com.copulaapp.mediaws.UploadModel;
import com.copulaapp.webservice.util.RP;
import com.copulaapp.webservice.util.SecuredApi;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by heeleaz on 7/18/17.
 */

@Path("feed")
public class FeedMediaController {
    @POST
    @Path("/media/uploadImage")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @SecuredApi
    public void uploadMedia(
            @FormDataParam("file") final InputStream is,
            @FormDataParam("file") final FormDataBodyPart body,
            @Suspended final AsyncResponse asyncResponse) {
        new Thread(() -> {
            String mediaType = body.getMediaType().toString();
            String uploadId = UUID.randomUUID().toString();
            if (FeedMediaManager.putImage(uploadId, is, mediaType)) {
                UploadModel model = new UploadModel(uploadId);
                asyncResponse.resume(RP.
                        object(RP.SUCCESS, "Upload Successful", model));
            } else {
                asyncResponse.resume(RP.string(RP.FAILED, "Upload Failed"));
            }
        }).start();
    }

    @POST
    @Path("/media/uploadVideo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @SecuredApi
    public void uploadVideo(
            @FormDataParam("file") final InputStream is,
            @FormDataParam("file") final FormDataBodyPart body,
            @FormDataParam("thumbnail") final InputStream thumbnail,
            @Suspended final AsyncResponse asyncResponse) {
        new Thread(() -> {
            //String mediaType = body.getMediaType().toString();
            String uploadId = UUID.randomUUID().toString();
            boolean vu = FeedMediaManager.putVideo(uploadId, is);
            if (vu && FeedMediaManager.putVideoThumbnail(uploadId, thumbnail)) {
                UploadModel model = new UploadModel(uploadId);
                asyncResponse.resume(RP.
                        object(RP.SUCCESS, "Upload Successful", model));
            } else {
                asyncResponse.resume(RP.string(RP.FAILED, "Upload Failed"));
            }
        }).start();
    }
}
