ALTER TABLE deal
    ADD IF NOT EXISTS last_change_time timestamp;

UPDATE deal
SET last_change_time = now()
WHERE deal.last_change_time IS NULL;
