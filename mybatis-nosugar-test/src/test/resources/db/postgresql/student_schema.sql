/*
 * Copyright 2021-2023 NoSugarIce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
