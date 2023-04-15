CREATE TABLE IF NOT EXISTS deal
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    user_id integer,
    ticker text,
    side text,
    first_price double precision,
    second_price double precision,
    third_price double precision,
    fourth_price double precision,
    fifth_price double precision,
    average_price double precision,
    open boolean,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES mzrt_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);
