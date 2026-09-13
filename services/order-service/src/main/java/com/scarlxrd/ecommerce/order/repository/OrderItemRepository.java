package com.scarlxrd.ecommerce.order.repository;

import com.scarlxrd.ecommerce.order.entity.OrderItemEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OrderItemRepository
        implements PanacheRepositoryBase<OrderItemEntity, UUID> {

    public List<OrderItemEntity> findByOrderId(UUID orderId) {
        return list("order.id", orderId);
    }
}