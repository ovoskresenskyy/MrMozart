ALTER TABLE deal
    ADD IF NOT EXISTS closing_alert text;

UPDATE deal
SET closing_alert = ''
WHERE deal.closing_alert IS NULL;