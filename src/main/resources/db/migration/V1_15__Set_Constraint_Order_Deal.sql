ALTER TABLE mzrt_order
    ADD CONSTRAINT fk_order_deal FOREIGN KEY (deal_id) REFERENCES deal (id);