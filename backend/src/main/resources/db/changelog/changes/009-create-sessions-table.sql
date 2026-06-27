--liquibase formatted sql

--changeset fmi:009-create-sessions-table
CREATE TABLE sessions (

    id BIGSERIAL PRIMARY KEY,

    event_id BIGINT NOT NULL,

    title VARCHAR(255) NOT NULL,

    description TEXT,

    start_time TIMESTAMP NOT NULL,

    end_time TIMESTAMP NOT NULL,

    order_index INTEGER NOT NULL,

    location_room VARCHAR(255),

    type VARCHAR(50) NOT NULL,

    CONSTRAINT fk_sessions_event
        FOREIGN KEY (event_id)
        REFERENCES events(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_session_order
        UNIQUE(event_id, order_index)
);