CREATE TABLE IF NOT EXISTS percent_profit
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    ticker_id integer,
    strategy_id integer,
    value double precision,

    PRIMARY KEY (id)
);
