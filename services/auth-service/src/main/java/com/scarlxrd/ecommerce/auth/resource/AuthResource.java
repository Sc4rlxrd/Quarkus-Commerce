package com.scarlxrd.ecommerce.auth.resource;

import com.scarlxrd.ecommerce.auth.service.AuthService;
import com.scarlxrd.ecommerce.contracts.auth.request.LoginRequest;
import com.scarlxrd.ecommerce.contracts.auth.request.RefreshTokenRequest;
import com.scarlxrd.ecommerce.contracts.auth.request.RegisterRequest;
import com.scarlxrd.ecommerce.contracts.auth.response.TokenResponse;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthService authService) {this.authService = authService;}

    @POST
    @Path("/login")
    @PermitAll
    public TokenResponse login(@Valid LoginRequest request) {
        return authService.login(request);
    }

    @POST
    @Path("/register")
    @PermitAll
    public Response register(@Valid RegisterRequest request) {
        authService.register(request);

        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/refresh")
    @PermitAll
    public TokenResponse refresh(@Valid RefreshTokenRequest request) {
        return authService.refreshToken(request.refreshToken());
    }

    @POST
    @Path("/logout")
    @Authenticated
    public Response logout(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        authService.logout(authorizationHeader);

        return Response.ok().build();
    }
}