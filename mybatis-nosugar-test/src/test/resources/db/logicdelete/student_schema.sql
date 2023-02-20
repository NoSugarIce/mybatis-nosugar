DROP TABLE student IF EXISTS;
CREATE TABLE student
(
    id            VARCHAR(32) NOT NULL,
    name          VARCHAR(50) NOT NULL,
    disabled      INTEGER     NOT NULL,
    disabled_by   INTEGER NULL,
    disabled_name VARCHAR(32) NULL,
    PRIMARY KEY (id)
);
