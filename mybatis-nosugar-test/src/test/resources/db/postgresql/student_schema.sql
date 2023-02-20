DROP TABLE IF EXISTS student;
CREATE TABLE "student"
(
    "id"           varchar(32)  NOT NULL,
    "name"         varchar(20)  NOT NULL,
    "age"          int4         NOT NULL,
    "sex"          int4         NOT NULL,
    "sno"          varchar(20)  NOT NULL,
    "phone"        varchar(20)           DEFAULT NULL,
    "address"      varchar(100)          DEFAULT NULL,
    "card_balance" numeric(6, 2)         DEFAULT NULL,
    "status"       int4         NOT NULL DEFAULT 0,
    "version"      int4,
    "created_at"   timestamp(0) NOT NULL,
    "updated_at"   timestamp(0),
    "disabled_at"  timestamp(0),
    CONSTRAINT "student_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "student"
    OWNER TO "postgres";
