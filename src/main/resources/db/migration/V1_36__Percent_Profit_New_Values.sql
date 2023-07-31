ALTER TABLE percent_profit
    ADD IF NOT EXISTS number int;

UPDATE percent_profit
SET number = 1 WHERE percent_profit.number IS NULL;