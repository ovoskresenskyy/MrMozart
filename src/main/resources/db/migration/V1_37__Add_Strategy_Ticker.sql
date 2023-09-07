CREATE TABLE IF NOT EXISTS strategy_ticker
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    ticker_id integer,
    name text,
    strategy_id integer,
    percent_tp_1 double precision,
    percent_tp_2 double precision,
    percent_tp_3 double precision,
    percent_tp_4 double precision,
    percent_tp_5 double precision,
    stop_when_used boolean,

    PRIMARY KEY (id)
);

UPDATE strategy_ticker SET percent_tp_1 = 0 WHERE percent_tp_1 IS NULL;
UPDATE strategy_ticker SET percent_tp_2 = 0 WHERE percent_tp_2 IS NULL;
UPDATE strategy_ticker SET percent_tp_3 = 0 WHERE percent_tp_3 IS NULL;
UPDATE strategy_ticker SET percent_tp_4 = 0 WHERE percent_tp_4 IS NULL;
UPDATE strategy_ticker SET percent_tp_5 = 0 WHERE percent_tp_5 IS NULL;
UPDATE strategy_ticker SET stop_when_used = false WHERE stop_when_used IS NULL;