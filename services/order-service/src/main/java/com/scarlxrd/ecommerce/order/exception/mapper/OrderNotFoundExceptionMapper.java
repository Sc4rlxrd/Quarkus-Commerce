package com.scarlxrd.ecommerce.order.exception.mapper;

import com.scarlxrd.ecommerce.contracts.common.ErrorResponse;
import com.scarlxrd.ecommerce.order.exception.OrderNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Provider
public class OrderNotFoundExceptionMapper
        implements ExceptionMapper<OrderNotFoundException> {

    @Override
    public Response toResponse(OrderNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(
                        404,
                        "Not Found",
                        exception.getMessage(),
                        OffsetDateTime.now(ZoneOffset.UTC)
                ))
                .build();
    }
}