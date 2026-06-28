--changeset fmi:013-add-ticket-price-and-currency-to-events

ALTER TABLE events
ADD COLUMN ticket_price NUMERIC(10,2) NOT NULL DEFAULT 0;

ALTER TABLE events
ADD COLUMN currency VARCHAR(10);

ALTER TABLE events
ADD CONSTRAINT chk_event_price
CHECK (ticket_price >= 0);

ALTER TABLE events
ADD CONSTRAINT chk_event_currency
CHECK (
    ticket_price = 0
    OR currency IS NOT NULL
);