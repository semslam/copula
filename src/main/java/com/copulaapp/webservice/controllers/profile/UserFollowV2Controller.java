package com.copulaapp.webservice.controllers.profile;

import com.copulaapp.webservice.models.profile.FollowBoardDao;
import com.copulaapp.webservice.models.profile.UserProfileDao;
import com.copulaapp.webservice.models.profile.entry.UserProfileEntry;
import com.copulaapp.webservice.util.Pager;
import com.copulaapp.webservice.util.RP;
import com.copulaapp.webservice.util.SecuredApi;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.SQLException;

/**
 * Created by heeleeaz on 6/28/16.
 */
@Path("friends")
public class UserFollowV2Controller {
    private FollowBoardDao mDaoUserFriends = new FollowBoardDao();
    private UserProfileDao mUserAccountDAO = new UserProfileDao();

    @PUT
    @Path("/follow")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response follow(MultivaluedMap<String, String> qp) throws Exception {
        String userId = qp.getFirst("userId");
        String followUserId = qp.getFirst("followUserId");

        if (mDaoUserFriends.isFollowing(userId, followUserId)) {
            return RP.string(RP.EXISTS, "Already Following", null);
        } else if ((mDaoUserFriends.follow(userId, followUserId))) {
            UserProfileEntry fProfile = mUserAccountDAO.getProfile(followUserId);
            ProfileCloudMSGAware.pushFollowMessageAsync(userId, fProfile);
            return RP.object(RP.SUCCESS, "Following", null);
        } else {
            return RP.string(RP.FAILED, "Follow Failed", null);
        }
    }


    @DELETE
    @Path("/unFollow")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response unFollow(MultivaluedMap<String, String> qp) throws SQLException {
        String userId = qp.getFirst("userId");
        String followUserId = qp.getFirst("followUserId");

        if (mDaoUserFriends.unfollow(userId, followUserId)) {
            return RP.string(RP.SUCCESS, "Successful", null);
        } else {
            return RP.string(RP.FAILED, "Failed", null);
        }
    }

    @GET
    @Path("/getFollowings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFollowings(@Context UriInfo uriInfo) throws Exception {
        MultivaluedMap<String, String> qp = uriInfo.getQueryParameters();
        String userId = qp.getFirst("userId");
        String pageToken = qp.getFirst("pageToken");

        Pager friendList = mDaoUserFriends.getFollowings(userId, pageToken);
        if (friendList.packet != null && friendList.packet.size() > 0) {
            return RP.pager(RP.SUCCESS, "Success", friendList);
        } else {
            return RP.string(RP.EMPTY, "Empty", null);
        }
    }//end
}
