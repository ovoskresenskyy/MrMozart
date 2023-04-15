ALTER TABLE mzrt_order
    ADD IF NOT EXISTS deal_id integer,
    ADD IF NOT EXISTS price integer;