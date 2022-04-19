package com.copulaapp.webservice.controllers.support;

import com.copulaapp.webservice.models.support.SystemReportDao;
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
@Path("/support/systemreport")
public class SystemReportController {
    private SystemReportDao systemReportDao = new SystemReportDao();

    @PUT
    @Path("/newReport")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @SecuredApi
    public Response newReport(MultivaluedMap<String, String> qp) throws SQLException {
        String email = qp.getFirst("email");
        String deviceId = qp.getFirst("deviceId");
        String message = qp.getFirst("message");
        if (systemReportDao.postReport(email, deviceId, message)) {
            return RP.string(RP.SUCCESS, "Report Successful", null);
        } else {
            return RP.string(RP.FAILED, "Report failed", null);
        }
    }
}
