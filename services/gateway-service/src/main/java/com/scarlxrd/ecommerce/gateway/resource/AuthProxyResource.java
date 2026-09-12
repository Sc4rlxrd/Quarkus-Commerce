package com.scarlxrd.ecommerce.gateway.resource;

import com.scarlxrd.ecommerce.contracts.auth.request.LoginRequest;
import com.scarlxrd.ecommerce.contracts.auth.request.RefreshTokenRequest;
import com.scarlxrd.ecommerce.contracts.auth.request.RegisterRequest;
import com.scarlxrd.ecommerce.contracts.auth.response.TokenResponse;
import com.scarlxrd.ecommerce.gateway.client.AuthServiceClient;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthProxyResource {

    private final AuthServiceClient authServiceClient;

    public AuthProxyResource(@RestClient AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }

    @POST
    @Path("/register")
    @PermitAll
    public Response register(@Valid RegisterRequest request) {
        return authServiceClient.register(request);
    }

    @POST
    @Path("/login")
    @PermitAll
    public TokenResponse login(@Valid LoginRequest request) {
        return authServiceClient.login(request);
    }

    @POST
    @Path("/refresh")
    @PermitAll
    public TokenResponse refresh(@Valid RefreshTokenRequest request) {
        return authServiceClient.refresh(request);
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return authServiceClient.logout(authorizationHeader);
    }
}