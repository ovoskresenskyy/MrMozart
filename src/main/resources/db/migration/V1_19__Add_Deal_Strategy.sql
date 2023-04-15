ALTER TABLE deal
    ADD IF NOT EXISTS strategy text;

INSERT INTO deal (strategy)
VALUES ('mozart');
