package com.copulaapp.webservice.controllers.profile;

import com.copulaapp.webservice.models.profile.UserInterestDao;
import com.copulaapp.webservice.util.RP;
import com.copulaapp.webservice.util.SecuredApi;
import com.copulaapp.webservice.util.validator.Val;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * Created by heeleaz on 9/22/17.
 */
@Path("profile/interest")
public class UserInterestController {
    private final UserInterestDao interestDao = new UserInterestDao();

    @PUT
    @Path("/addInterest")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response addInterest(MultivaluedMap<String, String> qp) throws Exception {
        String userId = qp.getFirst("userId");
        String interest = Val.parseText(qp.getFirst("interest"));

        if (interestDao.addInterest(userId, interest)) {
            return RP.string(RP.SUCCESS, "Interest Updated");
        } else return RP.string(RP.FAILED, "Update Failed");
    }

    @GET
    @Path("/getUserInterest")
    @Produces(MediaType.APPLICATION_JSON)
    @SecuredApi
    public Response getInterest(@Context UriInfo uriInfo) throws Exception {
        MultivaluedMap<String, String> qp = uriInfo.getQueryParameters();

        String interest = interestDao.getUserInterest(qp.getFirst("userId"));
        return RP.string(RP.SUCCESS, "Interest Fetched", interest);
    }
}
