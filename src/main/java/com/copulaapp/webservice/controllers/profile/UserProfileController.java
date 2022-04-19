package com.copulaapp.webservice.controllers.profile;

import com.copulaapp.webservice.models.profile.FollowBoardDao;
import com.copulaapp.webservice.models.profile.UserProfileDao;
import com.copulaapp.webservice.models.profile.entry.UserProfileEntry;
import com.copulaapp.webservice.models.profile.notification.UserPresenceNoticor;
import com.copulaapp.webservice.util.Pager;
import com.copulaapp.webservice.util.RP;
import com.copulaapp.webservice.util.SecuredApi;
import com.copulaapp.webservice.util.validator.Val;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.SQLException;

/**
 * Created by heeleeaz on 6/28/16.
 */
@Path("profile")
public class UserProfileController {
    private final UserProfileDao userProfileDAO = new UserProfileDao();
    private final FollowBoardDao daoFollowBoard = new FollowBoardDao();

    @PUT
    @Path("/newProfile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response newProfile(MultivaluedMap<String, String> qp) throws SQLException {
        String userId = qp.getFirst("userId");
        String bio = Val.parseText(qp.getFirst("bio"));

        if (userProfileDAO.newProfile(userId, bio)) {
            return RP.string(RP.SUCCESS, "Profile Updated", null);
        } else {
            return RP.string(RP.FAILED, "Profile Update Failed", null);
        }
    }

    @GET
    @Path("/getProfile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@Context UriInfo uriInfo) throws Exception {
        MultivaluedMap<String, String> qp = uriInfo.getQueryParameters();
        String userId = qp.getFirst("userId");
        String peeperId = qp.getFirst("peeperId");

        UserProfileEntry model = userProfileDAO.getProfile(userId);
        if (model != null) {
            model.isFollowing = daoFollowBoard.isFollowing(peeperId, userId);
            return RP.object(RP.SUCCESS, "Success!!", model);
        } else {
            return RP.string(RP.EMPTY, "Profile Not Available", null);
        }
    }

    @GET
    @Path("/findProfile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findProfile(@Context UriInfo uriInfo) throws Exception {
        MultivaluedMap<String, String> qp = uriInfo.getQueryParameters();
        String q = qp.getFirst("q");
        String pageToken = qp.getFirst("pageToken");


        Pager<UserProfileEntry> e = userProfileDAO.searchProfile(q, pageToken);
        if (e != null && e.packet.size() > 0) {
            return RP.pager(RP.SUCCESS, "Success!!", e);
        } else {
            return RP.string(RP.EMPTY, "Empty", null);
        }
    }

    @POST
    @Path("/updateProfile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response updateProfile(MultivaluedMap<String, String> qp) throws SQLException {
        return newProfile(qp);
    }

    @POST
    @Path("announcePresence")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response announcePresence(MultivaluedMap<String, String> qp) throws Exception {
        UserPresenceNoticor notisor = new UserPresenceNoticor();
        notisor.setFriendsEmail(qp.getFirst("friendsEmail"));
        notisor.setUserEmail(qp.getFirst("email"));
        notisor.setUserFullname(qp.getFirst("fullname"));
        notisor.setSourcePlatform(qp.getFirst("sourcePlatform"));

        notisor.pushPresenceNotificationAsync();

        return RP.string(RP.SUCCESS, "Notification Pushed");
    }//end
}