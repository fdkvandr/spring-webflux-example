CREATE SCHEMA anime;

CREATE TABLE anime.anime (
    id SERIAL NOT NULL UNIQUE PRIMARY KEY,
    name VARCHAR NOT NULL
);

INSERT INTO anime.anime(name)
VALUES ('Hellsing');

CREATE TABLE anime.usr (
    id SERIAL NOT NULL UNIQUE PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    username VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    authorities VARCHAR(256) NOT NULL
);

INSERT INTO anime.usr (name, username, password, authorities)
VALUES ('Andrey', 'fdkvandr', '{bcrypt}$2a$10$WkNYtpOGZyWsxInS/zhml.ksL99/psFrtiRIMCbVrcDUjUCYyyZqW', 'ROLE_USER,ROLE_ADMIN'),
       ('Andrey', 'user', '{bcrypt}$2a$10$WkNYtpOGZyWsxInS/zhml.ksL99/psFrtiRIMCbVrcDUjUCYyyZqW', 'ROLE_USER');