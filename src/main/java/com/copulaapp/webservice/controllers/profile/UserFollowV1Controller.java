package com.copulaapp.webservice.controllers.profile;

import com.copulaapp.webservice.util.SecuredApi;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.SQLException;

/**
 * Created by heeleeaz on 6/28/16.
 */
@Path("friends")
public class UserFollowV1Controller {
    private UserFollowV2Controller controllerV2 = new UserFollowV2Controller();

    @PUT
    @Path("/addFriend")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response addFriend(MultivaluedMap<String, String> qp) throws Exception {
        qp.addFirst("followUserId", qp.getFirst("friendId"));
        return controllerV2.follow(qp);
    }

    @POST
    @Path("/followFriend")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @SecuredApi
    public Response followFriend(MultivaluedMap<String, String> qp) throws Exception {
        return addFriend(qp);
    }

    @DELETE
    @Path("/removeFriend")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response unFollow(MultivaluedMap<String, String> qp) throws SQLException {
        qp.addFirst("followUserId", qp.getFirst("friendId"));
        return controllerV2.unFollow(qp);
    }

    @POST
    @Path("/unfollowFriend")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @SecuredApi
    public Response unFollowUser(MultivaluedMap<String, String> qp) throws SQLException {
        return unFollow(qp);
    }

    @GET
    @Path("/getFriends")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFriends(@Context UriInfo uriInfo) throws Exception {
        return controllerV2.getFollowings(uriInfo);
    }//end
}
