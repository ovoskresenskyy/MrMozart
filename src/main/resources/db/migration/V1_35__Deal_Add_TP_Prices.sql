ALTER TABLE deal
    ADD IF NOT EXISTS take_price_1 double precision,
    ADD IF NOT EXISTS take_price_2 double precision,
    ADD IF NOT EXISTS take_price_3 double precision,
    ADD IF NOT EXISTS take_price_4 double precision,
    ADD IF NOT EXISTS take_price_5 double precision;

UPDATE deal
SET take_price_1 = 0 WHERE deal.take_price_1 IS NULL;

UPDATE deal
SET take_price_2 = 0 WHERE deal.take_price_2 IS NULL;

UPDATE deal
SET take_price_3 = 0 WHERE deal.take_price_3 IS NULL;

UPDATE deal
SET take_price_4 = 0 WHERE deal.take_price_4 IS NULL;

UPDATE deal
SET take_price_5 = 0 WHERE deal.take_price_5 IS NULL;
