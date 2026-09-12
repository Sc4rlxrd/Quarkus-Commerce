package com.scarlxrd.ecommerce.gateway.client;

import com.scarlxrd.ecommerce.contracts.auth.request.LoginRequest;
import com.scarlxrd.ecommerce.contracts.auth.request.RefreshTokenRequest;
import com.scarlxrd.ecommerce.contracts.auth.request.RegisterRequest;
import com.scarlxrd.ecommerce.contracts.auth.response.TokenResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "auth-service")
public interface AuthServiceClient {

    @POST
    @Path("/register")
    Response register(RegisterRequest request);

    @POST
    @Path("/login")
    TokenResponse login(LoginRequest request);

    @POST
    @Path("/refresh")
    TokenResponse refresh(RefreshTokenRequest request);

    @POST
    @Path("/logout")
    Response logout(
            @HeaderParam(HttpHeaders.AUTHORIZATION)
            String authorizationHeader
    );
}