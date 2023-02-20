DROP TABLE student IF EXISTS;
CREATE TABLE student
(
    id           VARCHAR(32)   NOT NULL,
    name         VARCHAR(50)   NOT NULL,
    age          INTEGER       NOT NULL,
    sex          INTEGER       NOT NULL,
    sno          VARCHAR(20)   NOT NULL,
    phone        VARCHAR(20)   NULL,
    address      VARCHAR(100)  NULL,
    card_balance DECIMAL(6, 2) NULL,
    status       INTEGER       NOT NULL,
    version      INTEGER       NULL,
    created_at   TIMESTAMP     NOT NULL,
    updated_at   TIMESTAMP     NULL,
    disabled_at  TIMESTAMP     NULL,
    PRIMARY KEY (id)
);
