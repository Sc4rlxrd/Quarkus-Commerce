package com.scarlxrd.ecommerce.product.exception.mapper;

import com.scarlxrd.ecommerce.contracts.common.ErrorResponse;
import com.scarlxrd.ecommerce.product.exception.SkuAlreadyExistsException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Provider
public class SkuAlreadyExistsExceptionMapper
        implements ExceptionMapper<SkuAlreadyExistsException> {

    @Override
    public Response toResponse(SkuAlreadyExistsException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(
                        409,
                        "Conflict",
                        exception.getMessage(),
                        OffsetDateTime.now(ZoneOffset.UTC)
                ))
                .build();
    }
}