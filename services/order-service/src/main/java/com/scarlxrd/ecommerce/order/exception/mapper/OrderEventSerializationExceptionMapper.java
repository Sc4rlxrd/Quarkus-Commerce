package com.scarlxrd.ecommerce.order.exception.mapper;

import com.scarlxrd.ecommerce.contracts.common.ErrorResponse;
import com.scarlxrd.ecommerce.order.exception.OrderEventSerializationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Provider
public class OrderEventSerializationExceptionMapper
        implements ExceptionMapper<OrderEventSerializationException> {

    @Override
    public Response toResponse(
            OrderEventSerializationException exception
    ) {
        return Response.status(
                        Response.Status.INTERNAL_SERVER_ERROR
                )
                .entity(new ErrorResponse(
                        500,
                        "Internal Server Error",
                        exception.getMessage(),
                        OffsetDateTime.now(ZoneOffset.UTC)
                ))
                .build();
    }
}