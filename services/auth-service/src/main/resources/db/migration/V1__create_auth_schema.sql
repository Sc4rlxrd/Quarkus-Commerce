CREATE TABLE users
(
    id            UUID PRIMARY KEY,
    name          VARCHAR(120)             NOT NULL,
    email         VARCHAR(255)             NOT NULL,
    password_hash VARCHAR(255)             NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE user_roles
(
    user_id UUID        NOT NULL,
    role    VARCHAR(30) NOT NULL,

    CONSTRAINT pk_user_roles
        PRIMARY KEY (user_id,
                     role),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT ck_user_roles_role
        CHECK (
            role IN
            (
             'USER',
             'CUSTOMER',
             'SELLER',
             'ADMIN'
                )
            )
);

CREATE INDEX idx_user_roles_role
    ON user_roles (role);