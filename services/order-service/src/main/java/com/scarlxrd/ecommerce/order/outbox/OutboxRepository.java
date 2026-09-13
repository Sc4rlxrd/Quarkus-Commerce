package com.scarlxrd.ecommerce.order.outbox;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OutboxRepository implements PanacheRepositoryBase<OutboxEvent, UUID> {

    public List<OutboxEvent> findPending(int limit) {
        return find(
                "status = ?1 order by createdAt asc",
                OutboxStatus.PENDING
        )
                .page(0, limit)
                .list();
    }
}