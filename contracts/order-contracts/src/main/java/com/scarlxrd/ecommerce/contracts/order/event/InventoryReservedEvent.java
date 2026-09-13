package com.scarlxrd.ecommerce.contracts.order.event;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryReservedEvent(
        UUID eventId,
        UUID orderId,
        UUID productId,
        int quantity,
        BigDecimal unitPrice
) {
}