CREATE TABLE mzrt_alert
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    name text,
    webhook text,
    secret text,
    side text,
    PRIMARY KEY (id)
);
CREATE TABLE mzrt_order
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    name text,
    secret text,
    side text,
    symbol text,
    PRIMARY KEY (id)
);
CREATE TABLE mzrt_user
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    name text,
    email text,
    password text,
    PRIMARY KEY (id)
);