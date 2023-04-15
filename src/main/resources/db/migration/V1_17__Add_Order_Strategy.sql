ALTER TABLE mzrt_order
    ADD IF NOT EXISTS strategy text;

INSERT INTO mzrt_order (strategy)
VALUES ('mozart');
