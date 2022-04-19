package com.copulaapp.webservice.controllers.support;

import com.copulaapp.webservice.models.support.UserFeedbackDao;
import com.copulaapp.webservice.util.RP;
import com.copulaapp.webservice.util.SecuredApi;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

/**
 * Created by heeleaz on 12/11/16.
 */
@Path("/support/userfeedback")
public class UserFeedbackController {
    private UserFeedbackDao mDao = new UserFeedbackDao();

    @PUT
    @Path("/newFeedback")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response newFeedback(MultivaluedMap<String, String> qp) throws SQLException {
        String email = qp.getFirst("email");
        String deviceId = qp.getFirst("deviceId");
        String message = qp.getFirst("message");
        String feature = qp.getFirst("feature");
        if (mDao.deviceFeatureFeedback(email, deviceId, feature, message)) {
            return RP.string(RP.SUCCESS, "Report Successful", null);
        } else {
            return RP.string(RP.FAILED, "Report failed", null);
        }
    }
}
