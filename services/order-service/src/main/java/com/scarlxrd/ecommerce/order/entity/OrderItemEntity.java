package com.scarlxrd.ecommerce.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    public OrderEntity order;

    @Column(name = "product_id", nullable = false)
    public UUID productId;

    @Column(nullable = false)
    public Integer quantity;

    @Column(name = "unit_price", precision = 19, scale = 2)
    public BigDecimal unitPrice;

    @Column(name = "subtotal", precision = 19, scale = 2)
    public BigDecimal subtotal;
}