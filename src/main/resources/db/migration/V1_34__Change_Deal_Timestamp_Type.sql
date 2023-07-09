ALTER TABLE deal
    DROP COLUMN last_change_time,
    ADD IF NOT EXISTS last_change_time timestamp;

UPDATE deal
SET last_change_time = CURRENT_TIMESTAMP
WHERE deal.last_change_time IS NULL;
