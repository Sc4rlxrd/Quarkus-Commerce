package com.scarlxrd.ecommerce.order.outbox;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@ApplicationScoped
public class OutboxRelay {

    private static final Logger log = Logger.getLogger(OutboxRelay.class);

    private static final int BATCH_SIZE = 50;
    private static final int MAX_RETRIES = 5;

    private final OutboxRepository outboxRepository;

    @Channel("outbox-events")
    MutinyEmitter<String> emitter;

    public OutboxRelay(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(every = "5s")
    @Transactional
    void publishPendingEvents() {

        List<OutboxEvent> events = outboxRepository.findPending(BATCH_SIZE);

        if (events.isEmpty()) {
            return;
        }

        log.debugf("Processing outbox events count=%d", events.size());

        for (OutboxEvent event : events) {
            publish(event);
        }
    }

    private void publish(OutboxEvent event) {
        try {

            OutgoingRabbitMQMetadata metadata = OutgoingRabbitMQMetadata.builder().withRoutingKey(event.routingKey).build();

            Message<String> message = Message.of(event.payload).addMetadata(metadata);

            emitter.sendMessage(message).await().indefinitely();

            event.status = OutboxStatus.PUBLISHED;
            event.publishedAt = OffsetDateTime.now(ZoneOffset.UTC);

            log.infof("Outbox event published eventId=%s aggregateId=%s eventType=%s routingKey=%s", event.id, event.aggregateId, event.eventType, event.routingKey);

        } catch (
                Exception exception) {

            event.retryCount++;

            if (event.retryCount >= MAX_RETRIES) {
                event.status = OutboxStatus.FAILED;
            }

            log.errorf(exception, "Failed to publish outbox event eventId=%s retry=%d status=%s", event.id, event.retryCount, event.status);
        }
    }
}