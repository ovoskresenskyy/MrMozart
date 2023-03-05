ALTER TABLE mzrt_order
    ADD user_id integer;

ALTER TABLE mzrt_order
    ADD CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES mzrt_user (id);