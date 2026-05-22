CREATE TABLE event_categories (
    event_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,

    PRIMARY KEY (event_id, category_id),

    CONSTRAINT fk_event_categories_event
        FOREIGN KEY (event_id)
        REFERENCES events(id),

    CONSTRAINT fk_event_categories_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
);