CREATE SCHEMA anime;

CREATE TABLE anime.anime (
     id SERIAL NOT NULL UNIQUE PRIMARY KEY,
     name VARCHAR NOT NULL
);

INSERT INTO anime.anime(name)
VALUES ('Hellsing');