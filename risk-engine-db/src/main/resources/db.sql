use risk;

-- auto-generated definition
create table metric
(
    id               bigint auto_increment comment '主键，自增ID'
        primary key,
    incident_code    varchar(64)                        not null comment '事件编码',
    indicator_code   varchar(64)                        not null comment '指标编码',
    indicator_name   varchar(128)                       not null comment '指标名称',
    indicator_value  varchar(256)                       not null comment '指标值 示例',
    indicator_desc   varchar(128)                       not null comment '指标描述',
    indicator_source tinyint                            not null comment '指标来源 枚举',
    indicator_type   tinyint                            not null comment '数据类型 枚举',
    operator         varchar(64)                        not null comment '操作人',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_indicator_code
        unique (incident_code, indicator_code) comment '唯一索引，确保事件code不重复'
)
    comment '风控指标表';

create index idx_incident_code
    on metric (incident_code)
    comment '索引，按事件code搜索';

create index task_incident_code
    on metric (incident_code)
    comment '索引，按事件code搜索';


-- auto-generated definition
create table blockchain_block
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

-- auto-generated definition
create table crawler_task
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
    on crawler_task (incident_code, status)
    comment '索引，按事件code搜索';

-- auto-generated definition
create table engine_result
(
    id                   bigint auto_increment
        primary key,
    incident_code        varchar(255) not null comment '关联事件编码',
    incident_name        varchar(255) not null comment '事件名称',
    request_payload      text         null comment '请求载荷（JSON报文',
    hit_online_rules     text         null comment '命中上线策略集合',
    hit_mock_rules       text         null comment '命中模拟策略集合',
    rule_code            varchar(255) null comment '匹配规则编码 分数最高',
    rule_name            varchar(255) null comment '规则名称',
    rule_status          int          null comment '规则状态（0：删除，1：上线，2：下线，3：模拟）',
    rule_score           int          null comment '风险分数',
    rule_decision_result varchar(255) null comment '决策结果',
    rule_label           varchar(255) null comment '标签',
    rule_penalty_action  varchar(255) null comment '处罚措施',
    rule_version         varchar(255) null comment '规则版本号',
    create_time          datetime     not null comment '创建时间'
);

-- auto-generated definition
create table incident
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

-- auto-generated definition
create table list_data
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

-- auto-generated definition
create table list_library
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

-- auto-generated definition
create table penalty
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

-- auto-generated definition
create table penalty_record
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
    on penalty_record (status, retry)
    comment '复合索引，加速按状态和重试次数查询';

-- auto-generated definition
create table rule
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

-- auto-generated definition
create table rule_version
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

-- auto-generated definition
create table transfer_record
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
    on transfer_record (chain, token);

create index idx_hash
    on transfer_record (hash);

create index idx_receive_address
    on transfer_record (receive_address);

create index idx_send_address
    on transfer_record (send_address);