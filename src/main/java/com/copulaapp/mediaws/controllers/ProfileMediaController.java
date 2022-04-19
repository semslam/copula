package com.copulaapp.mediaws.controllers;

import com.copulaapp.mediaws.ProfileMediaManager;
import com.copulaapp.webservice.util.RP;
import com.copulaapp.webservice.util.SecuredApi;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * Created by heeleaz on 7/17/17.
 */
@Path("profile")
public class ProfileMediaController {
    @POST
    @Path("/image/update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @SecuredApi
    public void updateProfileImage(
            @FormDataParam("userId") final String userId,
            @FormDataParam("file") final InputStream is,
            @FormDataParam("file") FormDataContentDisposition fileDisposition,
            @Suspended final AsyncResponse asyncResponse) {
        new Thread(() -> {
            if (ProfileMediaManager.putImage(userId, is)) {
                asyncResponse.resume(RP.string(RP.SUCCESS, "Update Successful"));
            } else {
                asyncResponse.resume(RP.string(RP.FAILED, "Update Failed"));
            }
        }).start();
    }
}
