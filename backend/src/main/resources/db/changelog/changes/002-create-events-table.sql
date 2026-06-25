--liquibase formatted sql

--changeset fmi:002-create-events-table
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,

    organizer_id BIGINT NOT NULL,

    title VARCHAR(255) NOT NULL,

    description VARCHAR(255),

    venue VARCHAR(255) NOT NULL,

    start_time TIMESTAMP NOT NULL,

    end_time TIMESTAMP NOT NULL,

    capacity INTEGER NOT NULL,

    available_tickets INTEGER NOT NULL,

    CONSTRAINT fk_events_organizer
            FOREIGN KEY (organizer_id)
            REFERENCES users (id)
);