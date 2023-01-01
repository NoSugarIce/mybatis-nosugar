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
