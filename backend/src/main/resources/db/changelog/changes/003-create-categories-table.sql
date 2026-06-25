--liquibase formatted sql

--changeset fmi:003-create-categories-table
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL UNIQUE
);