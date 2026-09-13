package com.scarlxrd.ecommerce.product.resource;

import com.scarlxrd.ecommerce.contracts.product.request.CreateProductRequest;
import com.scarlxrd.ecommerce.contracts.product.request.UpdateProductRequest;
import com.scarlxrd.ecommerce.contracts.product.response.ProductResponse;
import com.scarlxrd.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @GET
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }

    @GET
    @Path("/{id}")
    public ProductResponse findById(@PathParam("id") UUID id
    ) {
        return productService.findById(id);
    }

    @POST
    public Response create(
            @Valid CreateProductRequest request,
            @HeaderParam("X-User-Id") UUID sellerId
    ) {
        ProductResponse response = productService.create(request, sellerId);

        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PATCH
    @Path("/{id}")
    public ProductResponse update(
            @PathParam("id") UUID id,
            @Valid UpdateProductRequest request,
            @HeaderParam("X-User-Id") UUID sellerId,
            @HeaderParam("X-User-Roles") String roles
    ) {
        boolean admin = hasRole(roles, "ADMIN");

        return productService.update(
                id,
                request,
                sellerId,
                admin
        );
    }

    @DELETE
    @Path("/{id}")
    public Response deactivate(
            @PathParam("id") UUID id,
            @HeaderParam("X-User-Id") UUID sellerId,
            @HeaderParam("X-User-Roles") String roles
    ) {
        boolean admin = hasRole(roles, "ADMIN");

        productService.deactivate(
                id,
                sellerId,
                admin
        );

        return Response.noContent().build();
    }

    private boolean hasRole(
            String roles,
            String role
    ) {
        if (roles == null || roles.isBlank()) {
            return false;
        }

        return List.of(roles.split(","))
                .stream()
                .map(String::trim)
                .anyMatch(role::equals);
    }
}