package com.scarlxrd.ecommerce.gateway.client;

import com.scarlxrd.ecommerce.contracts.product.request.CreateProductRequest;
import com.scarlxrd.ecommerce.contracts.product.request.UpdateProductRequest;
import com.scarlxrd.ecommerce.contracts.product.response.ProductResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.UUID;

@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "product-service")
public interface ProductServiceClient {

    @GET
    List<ProductResponse> findAll();

    @GET
    @Path("/{id}")
    ProductResponse findById(
            @PathParam("id") UUID id
    );

    @POST
    Response create(
            CreateProductRequest request,
            @HeaderParam("X-User-Id") String userId,
            @HeaderParam("X-User-Roles") String roles
    );

    @PATCH
    @Path("/{id}")
    ProductResponse update(
            @PathParam("id") UUID id,
            UpdateProductRequest request,
            @HeaderParam("X-User-Id") String userId,
            @HeaderParam("X-User-Roles") String roles
    );

    @DELETE
    @Path("/{id}")
    Response deactivate(
            @PathParam("id") UUID id,
            @HeaderParam("X-User-Id") String userId,
            @HeaderParam("X-User-Roles") String roles
    );
}