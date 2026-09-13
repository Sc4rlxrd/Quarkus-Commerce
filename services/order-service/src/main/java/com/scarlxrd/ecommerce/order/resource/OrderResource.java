package com.scarlxrd.ecommerce.order.resource;

import com.scarlxrd.ecommerce.contracts.order.request.CreateOrderRequest;
import com.scarlxrd.ecommerce.contracts.order.response.OrderResponse;
import com.scarlxrd.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @POST
    public Response create(
            @Valid CreateOrderRequest request,
            @HeaderParam("X-User-Id") UUID customerId
    ) {
        OrderResponse response =
                orderService.create(request, customerId);

        return Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }
}