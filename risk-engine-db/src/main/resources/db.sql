use risk;
create table if not exists risk.blockchain_block
(
    id                  bigint auto_increment
        primary key,
    hash                varchar(64) not null comment '区块哈希',
    height              int         not null comment '区块高度',
    version             int         not null comment '版本',
    timestamp           bigint      not null comment '时间戳',
    tx_count            int         not null comment '交易数量',
    size                int         not null comment '区块大小',
    weight              int         not null comment '区块重量',
    merkle_root         varchar(64) not null comment 'merkle root hash',
    previous_block_hash varchar(64) not null comment '父区块哈希',
    median_time         bigint      null comment '中位时间',
    nonce               bigint      not null comment '随机数',
    bits                int         not null comment '难度目标',
    difficulty          bigint      not null comment '区块难度',
    coin                varchar(32) not null comment '币种',
    chain               varchar(32) not null comment '链',
    create_time         datetime    not null comment '创建时间'
)
    comment '比特币区块信息表';

create table if not exists risk.crawler_task
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    flow_no         varchar(128) not null comment '流水号',
    request_payload text         null comment '请求参数',
    incident_code   varchar(64)  not null comment '事件code',
    status          int          not null comment '任务状态 0未同步风控 1已同步风控',
    retry           int          not null comment '重试次数',
    create_time     datetime     not null comment '创建时间',
    update_time     datetime     not null comment '更新时间'
)
    comment '任务表';

create index index_incident_code_status
    on risk.crawler_task (incident_code, status)
    comment '索引，按事件code搜索';

create table if not exists risk.engine_result
(
    id               bigint auto_increment
        primary key,
    flow_no          varchar(128) not null comment '业务流水号',
    risk_flow_no     varchar(64)  not null comment '风控流水号',
    incident_code    varchar(64)  not null comment '事件编码',
    incident_name    varchar(128) not null comment '事件名称',
    request_payload  json         null comment '请求载荷（JSON报文)',
    primary_element  json         null comment '基本要素',
    metric           json         null comment '使用的指标',
    extra            json         null comment '扩展数据',
    hit_online_rules json         null comment '命中上线策略集合',
    hit_mock_rules   json         null comment '命中模拟策略集合',
    primary_rule     json         null comment '命中主规则',
    decision_result  varchar(16)  null comment '决策结果',
    execution_time   bigint       null comment '决策耗时',
    create_time      datetime     not null comment '创建时间'
);

create table if not exists risk.incident
(
    id                 bigint auto_increment
        primary key,
    incident_code      varchar(255) not null comment '事件编码',
    incident_name      varchar(255) not null comment '事件名称',
    request_payload    text         null comment '请求载荷（JSON报文）',
    decision_result    varchar(255) not null comment '处置方式 0拒绝 1成功 可配置',
    status             int          not null comment '状态（0：删除，1：上线，2：下线）',
    responsible_person varchar(255) not null comment '责任人',
    operator           varchar(255) not null comment '操作人',
    create_time        datetime     not null comment '创建时间',
    update_time        datetime     not null comment '修改时间',
    constraint incident_code
        unique (incident_code)
);

create table if not exists risk.k_line
(
    id                     bigint auto_increment comment '主键'
        primary key,
    symbol                 varchar(20)                         not null comment '交易币对',
    `interval`             varchar(20)                         not null comment '时间间隔',
    open_time              bigint                              not null comment '开盘时间',
    open                   decimal(20, 8)                      not null comment '开盘价',
    high                   decimal(20, 8)                      not null comment '最高价',
    low                    decimal(20, 8)                      not null comment '最低价',
    close                  decimal(20, 8)                      not null comment '收盘价',
    volume                 decimal(20, 8)                      not null comment '成交量',
    close_time             bigint                              not null comment '收盘时间',
    quote_volume           decimal(20, 8)                      not null comment '成交额',
    trade_count            int                                 not null comment '成交笔数',
    taker_buy_volume       decimal(20, 8)                      not null comment '主动买入成交量',
    taker_buy_quote_volume decimal(20, 8)                      not null comment '主动买入成交额',
    create_time            timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    update_time            timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint unique_symbol_open_close
        unique (symbol, open_time)
)
    comment 'K线数据表';

create table if not exists risk.list_data
(
    id                bigint auto_increment comment '主键ID'
        primary key,
    list_library_code varchar(64)          not null comment '名单库编码',
    list_library_name varchar(128)         not null comment '名单库名称',
    list_name         varchar(128)         not null comment '名单名称',
    list_code         varchar(64)          not null comment '名单编码',
    list_value        varchar(256)         not null comment '名单值',
    list_desc         varchar(256)         not null comment '名单描述',
    status            tinyint(1) default 0 not null comment '状态（0：未启用，1：已启用）',
    list_type         tinyint    default 0 not null comment '名单类型（1：地址，2：ip 3：设备 4：uid）',
    operator          varchar(64)          not null comment '操作者',
    create_time       datetime             not null comment '创建时间',
    update_time       datetime             not null comment '更新时间',
    constraint uk_list_library_code
        unique (list_library_code, list_code) comment '唯一索引，确保处罚编码不重复'
)
    comment '名单数据表';

create table if not exists risk.list_library
(
    id                bigint auto_increment comment '主键，自增ID'
        primary key,
    list_library_code varchar(64)                          not null comment '名单库编码',
    list_library_name varchar(128)                         not null comment '名单库名称',
    list_library_desc text                                 null comment '名单描述',
    status            tinyint(1) default 0                 not null comment '状态，0未启用，1启用',
    list_category     tinyint(1)                           not null comment '名单库类别',
    operator          varchar(64)                          not null comment '操作人',
    create_time       datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_list_library_code
        unique (list_library_code) comment '唯一索引，确保名单编码不重复'
)
    comment '风控名单库表';

create table if not exists risk.metric
(
    id            bigint auto_increment comment '主键，自增ID'
        primary key,
    incident_code varchar(64)                        not null comment '事件编码',
    metric_code   varchar(64)                        not null comment '指标编码',
    metric_name   varchar(128)                       not null comment '指标名称',
    sample_value  varchar(256)                       not null comment '指标值 示例',
    metric_desc   varchar(128)                       not null comment '指标描述',
    metric_source tinyint                            not null comment '指标来源 枚举',
    metric_type   tinyint                            not null comment '数据类型 枚举',
    operator      varchar(64)                        not null comment '操作人',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_metric_code
        unique (incident_code, metric_code) comment '唯一索引，确保事件code不重复'
)
    comment '风控指标表';

create index idx_incident_code
    on risk.metric (incident_code)
    comment '索引，按事件code搜索';

create table if not exists risk.penalty_action
(
    id                  bigint auto_increment comment '主键，自增ID'
        primary key,
    penalty_code        varchar(50)                          not null comment '处罚编码',
    penalty_name        varchar(100)                         not null comment '处罚名称',
    penalty_def         varchar(255)                         null comment '处罚定义',
    penalty_description text                                 null comment '处罚描述',
    penalty_json        text                                 null comment '处罚报文',
    status              tinyint(1) default 0                 not null comment '状态，0=未启用，1=启用',
    operator            varchar(64)                          null comment '操作人',
    create_time         datetime   default CURRENT_TIMESTAMP not null comment '创建时间，记录处罚记录的生成时间',
    update_time         datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间，记录处罚记录的最后修改时间',
    constraint uk_penalty_code
        unique (penalty_code) comment '唯一索引，确保处罚编码不重复'
)
    comment '风控处罚定义表';

create table if not exists risk.penalty_record
(
    id                  bigint auto_increment comment '主键，自增ID'
        primary key,
    flow_no             varchar(64)                          not null comment '业务流水号',
    rule_code           varchar(64)                          not null comment '规则编码',
    rule_name           varchar(128)                         not null comment '规则名称',
    incident_code       varchar(64)                          not null comment '事件编码',
    incident_name       varchar(128)                         not null comment '事件名称',
    penalty_code        varchar(64)                          not null comment '处罚编码',
    penalty_name        varchar(128)                         not null comment '处罚名称',
    penalty_def         varchar(255)                         null comment '处罚接口定义',
    penalty_description text                                 null comment '处罚描述',
    penalty_json        text                                 null comment '处罚报文',
    penalty_reason      varchar(128)                         null comment '处罚原因',
    penalty_result      varchar(128)                         null comment '执行结果',
    status              tinyint(1) default 0                 not null comment '处罚状态，0=待执行，1=成功，2=执行中，3=失败',
    retry               int        default 0                 not null comment '重试次数',
    penalty_time        datetime                             not null comment '处罚执行时间',
    create_time         datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time         datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '风控处罚表，存储处罚记录并支持定时任务执行';

create index idx_status_retry
    on risk.penalty_record (status, retry)
    comment '复合索引，加速按状态和重试次数查询';

create table if not exists risk.rule
(
    id                 bigint auto_increment
        primary key,
    incident_code      varchar(64)  not null comment '关联事件编码',
    rule_code          varchar(64)  not null comment '规则编码',
    rule_name          varchar(128) not null comment '规则名称',
    status             int          not null comment '状态（0：删除，1：上线，2：下线，3：模拟）',
    groovy_script      text         null comment 'groovy可执行表达式',
    json_script        text         null comment '特征json数组',
    logic_script       varchar(128) null comment '逻辑表达式 1 && 2 || 3',
    score              int          not null comment '风险分数',
    decision_result    varchar(2)   not null comment '决策结果 0拒绝 1成功',
    expiry_time        int          null comment '失效时间 单位h',
    label              varchar(64)  null comment '标签文本',
    penalty_action     text         null comment '处罚措施',
    version            varchar(64)  null comment '规则版本号',
    responsible_person varchar(64)  not null comment '责任人',
    operator           varchar(64)  not null comment '操作人',
    create_time        datetime     not null comment '创建时间',
    update_time        datetime     not null comment '修改时间',
    constraint rule_code
        unique (rule_code)
);

create table if not exists risk.rule_version
(
    id            bigint auto_increment
        primary key,
    rule_code     varchar(64)  not null comment '规则编码',
    status        tinyint(1)   not null comment '状态（0：删除，1：上线，2：下线，3：模拟）',
    groovy_script text         null comment 'groovy可执行表达式',
    json_script   text         null comment '特征json数组',
    logic_script  varchar(255) null comment '逻辑表达式 1 && 2 || 3',
    version       varchar(64)  not null comment '版本',
    operator      varchar(64)  not null comment '操作人',
    create_time   datetime     not null comment '创建时间',
    update_time   datetime     not null comment '修改时间',
    constraint rule_code_version
        unique (rule_code, version)
);

create table if not exists risk.transfer_record
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    send_address    varchar(255)    not null comment '发送地址',
    receive_address varchar(255)    not null comment '接收地址',
    amount          decimal(30, 18) not null comment '数量',
    u_amount        decimal(30, 18) null comment '折u价格',
    hash            varchar(255)    not null comment '交易哈希',
    height          int             not null comment '区块高度',
    chain           varchar(255)    not null comment '链',
    token           varchar(255)    not null comment '代币',
    fee             decimal(30, 18) null comment '手续费',
    transfer_time   bigint          null comment '交易转账时间（毫秒级时间戳）',
    created_time    datetime        not null comment '创建时间',
    status          int             not null comment '是否同步引擎执行（0或1表示状态）'
)
    comment '交易转账表';

create index idx_chain_token
    on risk.transfer_record (chain, token);

create index idx_hash
    on risk.transfer_record (hash);

create index idx_receive_address
    on risk.transfer_record (receive_address);

create index idx_send_address
    on risk.transfer_record (send_address);