package com.scarlxrd.ecommerce.order.exception.mapper;

import com.scarlxrd.ecommerce.contracts.common.ErrorResponse;
import com.scarlxrd.ecommerce.order.exception.InvalidOrderStateException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Provider
public class InvalidOrderStateExceptionMapper
        implements ExceptionMapper<InvalidOrderStateException> {

    @Override
    public Response toResponse(InvalidOrderStateException exception) {
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