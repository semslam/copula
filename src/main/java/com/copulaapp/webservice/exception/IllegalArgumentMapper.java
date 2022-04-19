package com.copulaapp.webservice.exception;

import com.copulaapp.webservice.util.RP;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by heeleeaz on 8/13/16.
 */
@Provider
public class IllegalArgumentMapper implements ExceptionMapper<IllegalArgumentException> {
    public Response toResponse(IllegalArgumentException e) {
        return RP.string(RP.WRONG_INPUT,
                "Input Mismatched", null);
    }
}
