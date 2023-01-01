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

DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`
(
    `id`           varchar(32)   NOT NULL COMMENT '主键',
    `name`         varchar(20)   NOT NULL COMMENT '姓名',
    `age`          int(3)        NOT NULL COMMENT '年龄',
    `sex`          int(1)        NOT NULL COMMENT '性别,0:男,1:女',
    `sno`          varchar(20)   NOT NULL COMMENT '学号',
    `phone`        varchar(20)   NULL     DEFAULT NULL COMMENT '电话号码',
    `address`      varchar(100)  NULL     DEFAULT NULL COMMENT '住址',
    `card_balance` decimal(6, 2) NULL     DEFAULT NULL COMMENT '学生卡余额',
    `status`       int(1)        NOT NULL DEFAULT 0 COMMENT '在学状态,0:在学,1退学',
    `version`      int(11)       NULL     DEFAULT NULL,
    `created_at`   datetime(0)   NOT NULL COMMENT '创建时间',
    `updated_at`   datetime(0)   NULL     DEFAULT NULL COMMENT '更新时间',
    `disabled_at`  datetime(0)   NULL     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB;
