ALTER TABLE mzrt_alert
    ADD IF NOT EXISTS strategy_id integer;

ALTER TABLE mzrt_alert
    ADD IF NOT EXISTS opening boolean;

UPDATE mzrt_alert
SET strategy_id = 1
WHERE strategy_id IS NULL;

UPDATE mzrt_alert
SET opening = true
WHERE mzrt_alert.name = '1S'
   OR mzrt_alert.name = '2S'
   OR mzrt_alert.name = '3S'
   OR mzrt_alert.name = '1L'
   OR mzrt_alert.name = '2L'
   OR mzrt_alert.name = '3L';

UPDATE mzrt_alert
SET opening = false
WHERE mzrt_alert.opening IS NULL;