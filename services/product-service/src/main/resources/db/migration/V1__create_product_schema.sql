CREATE TABLE products
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(180)   NOT NULL,
    description TEXT,
    sku         VARCHAR(80)    NOT NULL UNIQUE,
    price       NUMERIC(19, 2) NOT NULL,
    stock       INTEGER        NOT NULL,
    seller_id   UUID           NOT NULL,
    category    VARCHAR(100),
    active      BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ    NOT NULL,
    updated_at  TIMESTAMPTZ    NOT NULL,

    CONSTRAINT ck_products_price
        CHECK (price >=
               0),

    CONSTRAINT ck_products_stock
        CHECK (stock >=
               0)
);

CREATE INDEX idx_products_name
    ON products (name);

CREATE INDEX idx_products_seller_id
    ON products (seller_id);

CREATE INDEX idx_products_category
    ON products (category);