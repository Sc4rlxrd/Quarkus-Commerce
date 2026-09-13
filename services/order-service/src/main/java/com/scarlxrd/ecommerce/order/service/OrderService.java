package com.scarlxrd.ecommerce.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scarlxrd.ecommerce.contracts.order.event.InventoryReservationRequested;
import com.scarlxrd.ecommerce.contracts.order.request.CreateOrderRequest;
import com.scarlxrd.ecommerce.contracts.order.request.OrderItemRequest;
import com.scarlxrd.ecommerce.contracts.order.response.OrderResponse;
import com.scarlxrd.ecommerce.order.entity.OrderEntity;
import com.scarlxrd.ecommerce.order.entity.OrderItemEntity;
import com.scarlxrd.ecommerce.order.entity.OrderStatus;
import com.scarlxrd.ecommerce.order.mapper.OrderMapper;
import com.scarlxrd.ecommerce.order.outbox.OutboxEvent;
import com.scarlxrd.ecommerce.order.outbox.OutboxRepository;
import com.scarlxrd.ecommerce.order.outbox.OutboxStatus;
import com.scarlxrd.ecommerce.order.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class OrderService {

    private static final Logger log = Logger.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request, UUID customerId) {
        log.infof("Creating order customerId=%s items=%d", customerId, request.items().size());

        validateDuplicateProducts(request);

        OrderEntity order = new OrderEntity();

        order.customerId = customerId;
        order.status = OrderStatus.PENDING;
        order.totalAmount = null;

        for (OrderItemRequest itemRequest : request.items()) {

            OrderItemEntity item = new OrderItemEntity();

            item.productId = itemRequest.productId();
            item.quantity = itemRequest.quantity();

            // Price will only be known after
            // inventory reservation succeeds.
            item.unitPrice = null;
            item.subtotal = null;

            order.addItem(item);
        }

        orderRepository.persist(order);

        order.status = OrderStatus.RESERVING_STOCK;

        createInventoryReservationEvents(order);

        log.infof("Order created successfully id=%s customerId=%s items=%d status=%s", order.id, customerId, order.items.size(), order.status);

        return orderMapper.toResponse(order);
    }

    private void createInventoryReservationEvents(OrderEntity order) {
        for (OrderItemEntity item : order.items) {

            UUID eventId = UUID.randomUUID();

            InventoryReservationRequested event = new InventoryReservationRequested(eventId, order.id, item.productId, item.quantity);

            OutboxEvent outboxEvent = new OutboxEvent();

            outboxEvent.id = eventId;
            outboxEvent.aggregateId = order.id;
            outboxEvent.aggregateType = "ORDER";
            outboxEvent.eventType = "InventoryReservationRequested";
            outboxEvent.routingKey = "inventory.reservation.requested";
            outboxEvent.payload = serialize(event);
            outboxEvent.status = OutboxStatus.PENDING;
            outboxEvent.retryCount = 0;

            outboxRepository.persist(outboxEvent);

            log.debugf("Outbox event created eventId=%s orderId=%s productId=%s routingKey=%s", eventId, order.id, item.productId, outboxEvent.routingKey);
        }
    }

    private String serialize(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (
                JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize outbox event", exception);
        }
    }

    private void validateDuplicateProducts(CreateOrderRequest request) {
        Set<UUID> productIds = new HashSet<>();

        for (OrderItemRequest item : request.items()) {

            if (!productIds.add(item.productId())) {
                log.warnf("Order creation rejected because product is duplicated productId=%s", item.productId());

                throw new IllegalArgumentException("Duplicate product in order: " + item.productId());
            }
        }
    }
}