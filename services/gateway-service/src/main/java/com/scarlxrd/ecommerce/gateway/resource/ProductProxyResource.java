package com.scarlxrd.ecommerce.gateway.resource;

import com.scarlxrd.ecommerce.contracts.product.request.CreateProductRequest;
import com.scarlxrd.ecommerce.contracts.product.request.UpdateProductRequest;
import com.scarlxrd.ecommerce.contracts.product.response.ProductResponse;
import com.scarlxrd.ecommerce.gateway.client.ProductServiceClient;
import com.scarlxrd.ecommerce.gateway.security.GatewayAuthenticated;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.UUID;

@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductProxyResource {

    private final ProductServiceClient productServiceClient;

    public ProductProxyResource(
            @RestClient ProductServiceClient productServiceClient
    ) {
        this.productServiceClient = productServiceClient;
    }

    @GET
    public List<ProductResponse> findAll() {
        return productServiceClient.findAll();
    }

    @GET
    @Path("/{id}")
    public ProductResponse findById(
            @PathParam("id") UUID id
    ) {
        return productServiceClient.findById(id);
    }

    @POST
    @GatewayAuthenticated
    public Response create(
            @Valid CreateProductRequest request,
            @HeaderParam("X-User-Id") String userId,
            @HeaderParam("X-User-Roles") String roles
    ) {
        requireSellerOrAdmin(roles);

        return productServiceClient.create(
                request,
                userId,
                roles
        );
    }

    @PATCH
    @Path("/{id}")
    @GatewayAuthenticated
    public ProductResponse update(
            @PathParam("id") UUID id,
            @Valid UpdateProductRequest request,
            @HeaderParam("X-User-Id") String userId,
            @HeaderParam("X-User-Roles") String roles
    ) {
        requireSellerOrAdmin(roles);

        return productServiceClient.update(
                id,
                request,
                userId,
                roles
        );
    }

    @DELETE
    @Path("/{id}")
    @GatewayAuthenticated
    public Response deactivate(
            @PathParam("id") UUID id,
            @HeaderParam("X-User-Id") String userId,
            @HeaderParam("X-User-Roles") String roles
    ) {
        requireSellerOrAdmin(roles);

        return productServiceClient.deactivate(
                id,
                userId,
                roles
        );
    }

    private void requireSellerOrAdmin(String roles) {
        if (roles == null
                || (!roles.contains("SELLER")
                && !roles.contains("ADMIN"))) {

            throw new ForbiddenException(
                    "SELLER or ADMIN role required"
            );
        }
    }
}