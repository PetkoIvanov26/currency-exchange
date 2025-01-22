CREATE TABLE currency (
    id SERIAL PRIMARY KEY,
    code VARCHAR(3) NOT NULL UNIQUE
);

CREATE TABLE exchange_rate (
    id SERIAL PRIMARY KEY,
    currency_from_id BIGINT NOT NULL,
    currency_to_id BIGINT NOT NULL,
    exchange_rate DECIMAL(15, 6) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (currency_from_id) REFERENCES currency(id),
    FOREIGN KEY (currency_to_id) REFERENCES currency(id),
    UNIQUE (currency_from_id, currency_to_id)
);

CREATE TABLE currency_conversion (
    id BIGINT PRIMARY KEY,
    source_currency BIGINT NOT NULL,
    target_currency BIGINT NOT NULL,
    amount DECIMAL(19, 4),
    converted_amount DECIMAL(19, 4),
    transaction_id VARCHAR(255),
    timestamp TIMESTAMP,
    FOREIGN KEY (source_currency) REFERENCES currency(id),
    FOREIGN KEY (target_currency) REFERENCES currency(id)
);