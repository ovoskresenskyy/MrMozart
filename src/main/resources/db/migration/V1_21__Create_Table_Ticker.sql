CREATE TABLE IF NOT EXISTS ticker
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    user_id integer,
    name text,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES mzrt_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);
