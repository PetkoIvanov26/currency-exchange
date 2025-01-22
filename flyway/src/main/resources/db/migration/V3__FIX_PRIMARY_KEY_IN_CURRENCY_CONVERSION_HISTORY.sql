ALTER TABLE currency_conversion
DROP COLUMN IF EXISTS id;

ALTER TABLE currency_conversion
ADD COLUMN id SERIAL PRIMARY KEY;