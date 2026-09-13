package com.scarlxrd.ecommerce.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "customer_id", nullable = false)
    public UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    public OrderStatus status;

    @Column(name = "total_amount", precision = 19, scale = 2)
    public BigDecimal totalAmount;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    public List<OrderItemEntity> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    public OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public OffsetDateTime updatedAt;

    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.order = this;
    }

    public void removeItem(OrderItemEntity item) {
        items.remove(item);
        item.order = null;
    }

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}