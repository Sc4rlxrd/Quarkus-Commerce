package com.scarlxrd.ecommerce.gateway.resource;

import com.scarlxrd.ecommerce.gateway.security.GatewayAuthenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

@Path("/gateway")
@Produces(MediaType.APPLICATION_JSON)
public class GatewayTestResource {

    @GET
    @Path("/protected")
    @GatewayAuthenticated
    public Map<String, String> protectedRoute(
            @HeaderParam("X-User-Id") String userId,
            @HeaderParam("X-User-Email") String email,
            @HeaderParam("X-User-Roles") String roles
    ) {
        return Map.of(
                "userId", userId,
                "email", email,
                "roles", roles
        );
    }
}