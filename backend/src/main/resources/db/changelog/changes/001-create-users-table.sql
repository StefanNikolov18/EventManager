--liquibase formatted sql

--changeset fmi:001-create-users-table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,

    email VARCHAR(255) UNIQUE,

    hash_password VARCHAR(255) NOT NULL,

    first_name VARCHAR(255) NOT NULL,

    last_name VARCHAR(255) NOT NULL,

    role VARCHAR(50) NOT NULL
);