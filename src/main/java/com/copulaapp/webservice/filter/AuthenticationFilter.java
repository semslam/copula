package com.copulaapp.webservice.filter;

import com.copulaapp.webservice.controllers.admin.APITokenController;
import com.copulaapp.webservice.util.SecuredApi;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Created by heeleeaz on 7/24/16.
 */
@SecuredApi
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    public void filter(ContainerRequestContext containerRequest) throws IOException {
        String method = containerRequest.getMethod();
        String path = containerRequest.getUriInfo().getPath(true);

        if (method.equals("GET") && (path.equals("application.wadl") ||
                path.equals("application.wadl/xsd0.xsd"))) {
            return;
        }

        String authHeader = containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            containerRequest.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("username cannot access the resource.").build());
            return;
        }

        try {
            String token = authHeader.substring("Bearer".length()).trim();
            if (!APITokenController.authToken(token)) {
                System.out.println(authHeader);
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
        } catch (Exception e) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }
}

