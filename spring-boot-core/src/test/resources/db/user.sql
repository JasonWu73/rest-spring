DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    user_id     INT          NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    create_time DATETIME     NULL COMMENT '创建时间',
    username    VARCHAR(100) NULL COMMENT '用户名',
    birthday    DATE         NULL COMMENT '出生日期',
    is_enabled  TINYINT      NULL COMMENT '是否启用',
    PRIMARY KEY (user_id)
);