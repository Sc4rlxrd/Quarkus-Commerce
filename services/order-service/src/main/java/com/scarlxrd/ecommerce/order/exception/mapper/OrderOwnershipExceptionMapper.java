package com.scarlxrd.ecommerce.order.exception.mapper;

import com.scarlxrd.ecommerce.contracts.common.ErrorResponse;
import com.scarlxrd.ecommerce.order.exception.OrderOwnershipException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Provider
public class OrderOwnershipExceptionMapper
        implements ExceptionMapper<OrderOwnershipException> {

    @Override
    public Response toResponse(OrderOwnershipException exception) {
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