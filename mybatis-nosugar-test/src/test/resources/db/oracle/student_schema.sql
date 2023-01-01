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

DROP TABLE "DEMO"."STUDENT";
CREATE TABLE "DEMO"."STUDENT"
(
    "ID"           VARCHAR2(32) NOT NULL ENABLE,
    "NAME"         VARCHAR2(20) NOT NULL ENABLE,
    "AGE"          NUMBER(*, 0) NOT NULL ENABLE,
    "SEX"          NUMBER(*, 0) NOT NULL ENABLE,
    "SNO"          VARCHAR2(20) NOT NULL ENABLE,
    "PHONE"        VARCHAR2(20)  DEFAULT NULL,
    "ADDRESS"      VARCHAR2(100) DEFAULT NULL,
    "CARD_BALANCE" NUMBER(6, 2)  DEFAULT NULL,
    "STATUS"       NUMBER(*, 0) NOT NULL ENABLE,
    "VERSION"      NUMBER(*, 0) NOT NULL ENABLE,
    "CREATED_AT"   DATE         NOT NULL ENABLE,
    "UPDATED_AT"   DATE          DEFAULT NULL,
    "DISABLED_AT"  DATE          DEFAULT NULL,
    PRIMARY KEY ("ID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS NOCOMPRESS LOGGING TABLESPACE "USERS" ENABLE
) SEGMENT CREATION DEFERRED PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING TABLESPACE "USERS";
