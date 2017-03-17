package com.github.orelgenya;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Component
@Path("/secret")
public class SecretController {
    public static final String CONTENT_TYPE = MediaType.APPLICATION_JSON + ";charset=utf-8";

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(CONTENT_TYPE)
    public Response getSecret() {
        if (!securityContext.isUserInRole("Developer")) {
            String usename = securityContext.getUserPrincipal() == null ? "friend" :
                    securityContext.getUserPrincipal().getName();
            return Response.status(400).entity("Dear " + usename +
                    ", only developers are allowed to know secret!\nYour roles are: " +
                    (securityContext.getUserPrincipal() == null ?
                            "meaningless" :
                            ((JwtFilter.JwtPrincipal) securityContext.getUserPrincipal()).getRoles()))
                    .build();
        }
        return Response.ok("top-secret").build();
    }
}
