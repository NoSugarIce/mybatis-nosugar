DROP TABLE IF EXISTS Student;
CREATE TABLE Student
(
    id          varchar(32)   NOT NULL,
    name        varchar(20)   NOT NULL,
    age         smallint      NOT NULL,
    sex         smallint      NOT NULL,
    sno         varchar(20)   NOT NULL,
    phone       varchar(20)   NULL     DEFAULT NULL,
    address     varchar(100)  NULL     DEFAULT NULL,
    cardBalance decimal(6, 2) NULL     DEFAULT NULL,
    status      smallint      NOT NULL DEFAULT 0,
    version     int           NULL     DEFAULT NULL,
    createdAt   datetime      NOT NULL,
    updatedAt   datetime      NULL     DEFAULT NULL,
    disabledAt  datetime      NULL     DEFAULT NULL,
    PRIMARY KEY (id)
);