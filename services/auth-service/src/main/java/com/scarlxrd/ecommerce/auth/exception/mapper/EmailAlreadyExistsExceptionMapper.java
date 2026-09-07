package com.scarlxrd.ecommerce.auth.exception.mapper;

import com.scarlxrd.ecommerce.auth.exception.EmailAlreadyExistsException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class EmailAlreadyExistsExceptionMapper
        implements ExceptionMapper<EmailAlreadyExistsException> {

    @Override
    public Response toResponse(
            EmailAlreadyExistsException exception
    ) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(
                        400,
                        "Bad Request",
                        exception.getMessage()
                ))
                .build();
    }
}