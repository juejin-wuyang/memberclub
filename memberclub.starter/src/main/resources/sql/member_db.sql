-- 用于单测使用的 H2 数据库初始化

SET MODE MYSQL;

CREATE TABLE IF NOT EXISTS member_order (
    id BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '表自增主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    related_order_system_type INT(11)  NULL COMMENT '关联订单系统类型',
    related_order_id VARCHAR(128)  NULL COMMENT '关联订单  id',
    trade_id VARCHAR(128)  NOT NULL COMMENT '订单交易 id',
    renew_type INT(11)  NOT NULL COMMENT '续费类型 0 无续费,1 用户续费 2 系统自动续费',
    act_price_fen INT(11)  NULL COMMENT '订单金额',
    pay_amount_fen INT(11)  NULL COMMENT '实际支付金额',
    origin_price_fen INT(11)  NOT NULL COMMENT '原价金额',
    sale_price_fen INT(11)  NOT NULL COMMENT '原价金额',
    source INT(11)  NOT NULL COMMENT '开通来源',
    status INT(11)  NOT NULL COMMENT '主状态',
    perform_status INT(11)  NOT NULL COMMENT '履约状态',
    pay_status INT(11)  NOT NULL COMMENT '支付状态',
    pay_account VARCHAR(128)  NOT NULL COMMENT '支付账户',
    pay_account_type VARCHAR(32)  NOT NULL COMMENT '支付账户类型',
    pay_channel_type VARCHAR(128)  NOT NULL COMMENT '支付渠道类型',
    pay_online_type VARCHAR(32)  NOT NULL COMMENT '支付账户类型',
    pay_node_type VARCHAR(32)  NOT NULL COMMENT '支付渠道类型',
    merchant_id VARCHAR(128)  NOT NULL COMMENT '商户ID',
    pay_trade_no VARCHAR(128)  NOT NULL COMMENT '支付单号',
    pay_time BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '支付时间',
    extra TEXT NOT NULL COMMENT '扩展属性',
    stime BIGINT(20)  NULL DEFAULT '0' COMMENT '开始时间',
    etime BIGINT(20)  NULL DEFAULT '0' COMMENT '截止时间',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uniq_member_order (user_id, trade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS member_sub_order (
    id BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '表自增主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    related_order_system_type INT(11)  NULL COMMENT '关联订单系统类型',
    related_order_id VARCHAR(128)  NULL COMMENT '关联订单  id',
    trade_id VARCHAR(128)  NOT NULL COMMENT '订单交易 id',
    sub_trade_id BIGINT(20)  NOT NULL COMMENT '子单交易 id',
    sku_id BIGINT(20)  NOT NULL COMMENT 'skuId',
    act_price_fen INT(11)  NULL COMMENT '实付金额',
    origin_price_fen INT(11)  NULL COMMENT '原价金额',
    sale_price_fen INT(11)  NOT NULL COMMENT '原价金额',
    buy_count INT(11)  NOT NULL COMMENT '购买数量',
    status INT(11)  NOT NULL COMMENT '主状态',
    perform_status INT(11)  NOT NULL COMMENT '履约状态',
    extra TEXT NOT NULL COMMENT '扩展属性',
    stime BIGINT(20)   NULL DEFAULT '0' COMMENT '开始时间',
    etime BIGINT(20)   NULL DEFAULT '0' COMMENT '截止时间',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uniq_sub_order (user_id, trade_id, sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS once_task (
    id BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '表自增主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    task_group_id VARCHAR(128)  NOT NULL COMMENT '任务群组 id,由业务自定义',
    task_token VARCHAR(128)  NOT NULL COMMENT 'taskToken唯一键',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    task_type INT(11)  NOT NULL COMMENT '任务类型',
    status INT(11)  NOT NULL COMMENT '状态',
    task_content_class_name VARCHAR(256)  NOT NULL COMMENT '类型名称',
    content TEXT NOT NULL COMMENT '扩展属性',
    stime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '开始时间',
    etime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '截止时间',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uniq_once_task (user_id, task_token, task_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS member_ship (
    id BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '表自增主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    ship_type INT(11)  NOT NULL COMMENT '资格类型',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    trade_id VARCHAR(128)  NOT NULL COMMENT '主单交易 id',
    sub_trade_id VARCHAR(128)  NOT NULL COMMENT '子单交易 id',
    right_id INT(11)  NOT NULL COMMENT '权益Id',
    item_token VARCHAR(128)  NOT NULL COMMENT '履约项凭证',
    grant_code VARCHAR(128)  NULL COMMENT '发放批次码',
    used_count INT(11)  NOT NULL COMMENT '已使用数量',
    total_count INT(11)  NOT NULL COMMENT '最大可使用数量',
    status INT(11)  NOT NULL COMMENT '状态',
    extra TEXT NOT NULL COMMENT '扩展属性',
    stime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '开始时间',
    etime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '截止时间',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY key_member_ship_item (user_id, item_token),
    KEY key_period_stime (user_id, stime),
    KEY key_period_etime (user_id, etime),
    UNIQUE KEY uniq_grant_item (user_id, grant_code, biz_type)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ;

CREATE TABLE IF NOT EXISTS member_perform_item (
    id BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '表自增主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    trade_id VARCHAR(128)  NOT NULL COMMENT '主单交易 id',
    sub_trade_id VARCHAR(128)  NOT NULL COMMENT '子单交易 id',
    sku_id BIGINT(20)  NOT NULL COMMENT 'skuId',
    right_id INT(11)  NOT NULL COMMENT '权益Id',
    right_type INT(11)  NOT NULL COMMENT '权益类型',
    total_count INT(11)  NOT NULL COMMENT '资产、资格总数量',
    grant_type INT(11)  NOT NULL COMMENT '发放类型,直发 ,激活, 发资格',
    item_token VARCHAR(128)  NOT NULL COMMENT '履约项凭证',
    batch_code VARCHAR(128)  NULL COMMENT '发放批次码',
    provider_id INT(11)  NOT NULL COMMENT '履约方 id',
    phase INT(11)  NOT NULL COMMENT '期数',
    `cycle` INT(11)  NOT NULL COMMENT '周期数',
    buy_index INT(11)  NOT NULL COMMENT '购买序号',
    status INT(11)  NOT NULL COMMENT '状态',
    extra TEXT NOT NULL COMMENT '扩展属性',
    stime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '开始时间',
    etime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '截止时间',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY key_perform_item_batch (user_id, batch_code, trade_id),
    UNIQUE KEY uniq_perform_item (user_id, item_token, biz_type)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ;

CREATE TABLE IF NOT EXISTS aftersale_order (
    id BIGINT(20)  NOT NULL COMMENT '表主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    source INT(11)  NOT NULL COMMENT '售后来源',
    preview_token VARCHAR(128)  NOT NULL COMMENT '售后预览token',
    trade_id VARCHAR(128)  NOT NULL COMMENT '订单主单 id',
    operator VARCHAR(128)  NOT NULL COMMENT '操作人',
    act_pay_price_fen INT(11)  NOT NULL COMMENT '实付金额分',
    act_refund_price_fen INT(11)  NOT NULL COMMENT '实际退款金额分',
    recommend_refund_price_fen INT(11)  NOT NULL COMMENT '推荐的退款金额分',
    status INT(11)  NOT NULL COMMENT '售后状态',
    refund_type INT(11)  NOT NULL COMMENT '退款类型 全部退或部分退',
    refund_way INT(11)  NOT NULL COMMENT '退款方式 原路赔付、人工赔付',
    extra TEXT NOT NULL COMMENT '扩展属性',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY tradeid_key (user_id, trade_id),
    unique key preview_token_key(user_id, preview_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

CREATE TABLE IF NOT EXISTS outer_submit_record (
    id BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '表自增主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    trade_id VARCHAR(128)  NULL COMMENT '内部交易订单Id',
    outer_config_id VARCHAR(128)  NOT NULL COMMENT '外部系统配置ID',
    outer_id VARCHAR(128)  NULL COMMENT '外部系统凭证',
    outer_type INT(11)  NOT NULL COMMENT '外部下单类型',
    status INT(11)  NOT NULL COMMENT '状态',
    extra TEXT NOT NULL COMMENT '扩展属性',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uniq_outer (user_id, outer_id, outer_type)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;



CREATE TABLE IF NOT EXISTS order_remark (
    id BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '表自增主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    trade_id VARCHAR(128)  NULL COMMENT '交易Id',
    detail VARCHAR(512)  NOT NULL COMMENT '详情',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

CREATE TABLE IF NOT EXISTS redeem (
    id BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '表自增主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    related_id VARCHAR(128)  NULL COMMENT '关联交易凭证',
    code VARCHAR(128)  NOT NULL COMMENT '兑换码',
    status INT(11)  NOT NULL COMMENT '状态',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uniq_redeem (code)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;