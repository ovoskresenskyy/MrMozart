ALTER TABLE mzrt_alert
    ADD user_id integer;

ALTER TABLE mzrt_alert
    ADD CONSTRAINT fk_alert_user FOREIGN KEY (user_id) REFERENCES mzrt_user (id);