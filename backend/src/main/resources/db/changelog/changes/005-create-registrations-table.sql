--liquibase formatted sql

--changeset fmi:005-create-registrations-table
CREATE TABLE registrations (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    status VARCHAR(20) NOT NULL,
    registration_date TIMESTAMP NOT NULL DEFAULT NOW(),
    entry_code VARCHAR(50) NOT NULL UNIQUE,

    CONSTRAINT fk_registration_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_registration_event
        FOREIGN KEY (event_id) REFERENCES events(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_user_event UNIQUE (user_id, event_id)
);