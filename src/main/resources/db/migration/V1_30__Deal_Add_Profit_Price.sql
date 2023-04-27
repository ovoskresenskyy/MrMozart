ALTER TABLE deal
    ADD IF NOT EXISTS closing_price double precision;

UPDATE deal
SET closing_price = 0
WHERE deal.closing_price IS NULL;