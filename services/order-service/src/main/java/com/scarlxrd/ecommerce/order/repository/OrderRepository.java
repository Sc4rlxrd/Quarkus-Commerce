package com.scarlxrd.ecommerce.order.repository;

import com.scarlxrd.ecommerce.order.entity.OrderEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class OrderRepository implements PanacheRepositoryBase<OrderEntity, UUID> {

    public Optional<OrderEntity> findByIdAndCustomerId(UUID orderId,UUID customerId) {
        return find(
                "id = ?1 and customerId = ?2",
                orderId,
                customerId
        ).firstResultOptional();
    }

    public List<OrderEntity> findByCustomerId(UUID customerId) {
        return list(
                "customerId = ?1 order by createdAt desc",
                customerId
        );
    }
}