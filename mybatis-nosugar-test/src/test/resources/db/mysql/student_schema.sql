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
