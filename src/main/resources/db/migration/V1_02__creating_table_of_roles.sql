CREATE TABLE role
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    role text,
    user_id integer,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES "user" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

INSERT INTO "role" (role, user_id)
VALUES ('ADMIN', 1);
INSERT INTO "role" (role, user_id)
VALUES ('ADMIN', 2);