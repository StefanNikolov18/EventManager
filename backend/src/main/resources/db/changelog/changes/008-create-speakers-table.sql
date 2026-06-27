--liquibase formatted sql

--changeset fmi:008-create-speakers-table
CREATE TABLE speakers (
    id BIGSERIAL PRIMARY KEY,

    creater_id BIGINT NOT NULL,

    name VARCHAR(255) NOT NULL,

    biography TEXT,

    company_name VARCHAR(255),

    photo_url VARCHAR(500),

    website_url VARCHAR(500),

    CONSTRAINT fk_speakers_creater
        FOREIGN KEY (creater_id)
        REFERENCES users (id)
);