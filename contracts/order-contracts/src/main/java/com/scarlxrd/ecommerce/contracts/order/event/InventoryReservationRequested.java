package com.scarlxrd.ecommerce.contracts.order.event;

import java.util.UUID;

public record InventoryReservationRequested(
        UUID eventId,
        UUID orderId,
        UUID productId,
        int quantity
) {
}