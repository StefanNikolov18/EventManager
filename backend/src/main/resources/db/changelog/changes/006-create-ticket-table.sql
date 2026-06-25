--liquibase formatted sql

--changeset fmi:006-create-ticket-table
CREATE TABLE ticket (

    id BIGSERIAL PRIMARY KEY,
    registration_id BIGINT NOT NULL,

    price NUMERIC(10,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,

    CONSTRAINT fk_ticket_registration
                    FOREIGN KEY (registration_id) REFERENCES registrations(id)
                    ON DELETE CASCADE,

    CONSTRAINT uq_registration UNIQUE (registration_id),

    CHECK (price >= 0.0)

);