package com.copulaapp.webservice.exception;

import com.copulaapp.webservice.util.RP;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by heeleeaz on 8/13/16.
 */
@Provider
public class SuperExceptionMapper implements ExceptionMapper<Exception> {

    public Response toResponse(Exception e) {
        e.printStackTrace();
        return RP.string(RP.WRONG_INPUT,
                "Action Incomplete: " + e.getMessage(), null);
    }
}
