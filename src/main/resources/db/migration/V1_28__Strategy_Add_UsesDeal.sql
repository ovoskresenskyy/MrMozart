ALTER TABLE strategy
    ADD IF NOT EXISTS uses_deal boolean;

UPDATE strategy
SET uses_deal = true
WHERE strategy.id = 2;

UPDATE strategy
SET uses_deal = false
WHERE strategy.id = 1;
