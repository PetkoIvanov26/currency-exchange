ALTER TABLE exchange_rate
DROP CONSTRAINT IF EXISTS fk_currency_from_id;

ALTER TABLE exchange_rate
DROP CONSTRAINT IF EXISTS fk_currency_to_id;

ALTER TABLE currency_conversion
DROP CONSTRAINT IF EXISTS fk_source_currency;

ALTER TABLE currency_conversion
DROP CONSTRAINT IF EXISTS fk_target_currency;

ALTER TABLE exchange_rate
RENAME COLUMN currency_from_id TO source_currency_id;

ALTER TABLE exchange_rate
RENAME COLUMN currency_to_id TO target_currency_id;

ALTER TABLE currency_conversion
RENAME COLUMN source_currency TO source_currency_id;

ALTER TABLE currency_conversion
RENAME COLUMN target_currency TO target_currency_id;

ALTER TABLE exchange_rate
ADD CONSTRAINT fk_source_currency_id
FOREIGN KEY (source_currency_id) REFERENCES currency(id);

ALTER TABLE exchange_rate
ADD CONSTRAINT fk_target_currency_id
FOREIGN KEY (target_currency_id) REFERENCES currency(id);

ALTER TABLE currency_conversion
ADD CONSTRAINT fk_source_currency_id
FOREIGN KEY (source_currency_id) REFERENCES currency(id);

ALTER TABLE currency_conversion
ADD CONSTRAINT fk_target_currency_id
FOREIGN KEY (target_currency_id) REFERENCES currency(id);