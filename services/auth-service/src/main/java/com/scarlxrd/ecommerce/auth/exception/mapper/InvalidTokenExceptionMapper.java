package com.scarlxrd.ecommerce.auth.exception.mapper;

import com.scarlxrd.ecommerce.auth.exception.InvalidTokenException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidTokenExceptionMapper implements ExceptionMapper<InvalidTokenException> {

    @Override
    public Response toResponse(
            InvalidTokenException exception
    ) {
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse(
                        401,
                        "Unauthorized",
                        exception.getMessage()
                ))
                .build();
    }
}