ALTER TABLE deal
    ADD IF NOT EXISTS profit_price double precision;

UPDATE deal
SET profit_price = 0
WHERE deal.profit_price IS NULL;