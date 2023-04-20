CREATE TABLE IF NOT EXISTS strategy
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    name text,

    PRIMARY KEY (id)
);

INSERT INTO strategy (name)
VALUES ('Mozart');

INSERT INTO strategy (name)
VALUES ('Black Flag');

