CREATE TABLE outbox_events
(
    id             UUID PRIMARY KEY,
    aggregate_id   UUID         NOT NULL,
    aggregate_type VARCHAR(80)  NOT NULL,
    event_type     VARCHAR(120) NOT NULL,
    routing_key    VARCHAR(120) NOT NULL,
    payload        TEXT         NOT NULL,
    status         VARCHAR(30)  NOT NULL,
    retry_count    INTEGER      NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ  NOT NULL,
    updated_at     TIMESTAMPTZ  NOT NULL,
    published_at   TIMESTAMPTZ,

    CONSTRAINT ck_outbox_retry_count
        CHECK (retry_count >=
               0)
);

CREATE INDEX idx_outbox_status_created_at
    ON outbox_events (status,
                      created_at);

CREATE INDEX idx_outbox_aggregate_id
    ON outbox_events (aggregate_id);

CREATE INDEX idx_outbox_event_type
    ON outbox_events (event_type);