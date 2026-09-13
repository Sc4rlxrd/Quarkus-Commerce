package com.scarlxrd.ecommerce.product.exception.mapper;

import com.scarlxrd.ecommerce.contracts.common.ErrorResponse;
import com.scarlxrd.ecommerce.product.exception.ProductOwnershipException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Provider
public class ProductOwnershipExceptionMapper
        implements ExceptionMapper<ProductOwnershipException> {

    @Override
    public Response toResponse(ProductOwnershipException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorResponse(
                        403,
                        "Forbidden",
                        exception.getMessage(),
                        OffsetDateTime.now(ZoneOffset.UTC)
                ))
                .build();
    }
}