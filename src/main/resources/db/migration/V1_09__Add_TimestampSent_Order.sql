ALTER TABLE mzrt_order
    ADD timestamp_sent text;

ALTER TABLE mzrt_alert
    ADD pause integer;
