ALTER TABLE deal
    ADD IF NOT EXISTS strategy_id integer;

UPDATE deal
SET strategy_id = 1
WHERE strategy_id IS NULL;
