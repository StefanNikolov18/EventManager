--liquibase formatted sql

--changeset fmi:007-add-version-to-events
ALTER TABLE events ADD COLUMN version BIGINT NOT NULL DEFAULT 0;