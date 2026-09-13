package com.scarlxrd.ecommerce.order.mapper;

import com.scarlxrd.ecommerce.contracts.order.response.OrderItemResponse;
import com.scarlxrd.ecommerce.contracts.order.response.OrderResponse;
import com.scarlxrd.ecommerce.order.entity.OrderEntity;
import com.scarlxrd.ecommerce.order.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface OrderMapper {

    @Mapping(target = "status", expression = "java(order.status.name())")
    OrderResponse toResponse(OrderEntity order);

    OrderItemResponse toItemResponse(OrderItemEntity item);
}