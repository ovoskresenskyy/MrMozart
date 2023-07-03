ALTER TABLE deal
    ADD IF NOT EXISTS last_change_time text;

UPDATE deal
SET last_change_time = '01-01-2023 23:59:59'
WHERE deal.last_change_time IS NULL;
