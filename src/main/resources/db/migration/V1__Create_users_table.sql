CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(70)        NOT NULL
);