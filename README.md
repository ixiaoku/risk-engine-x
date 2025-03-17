# risk-engine-x 风控引擎系统X

## risk-engine-crawler 爬虫模块 抓取链上信息

## risk-engine-analysis 分析模块 分析清洗爬虫数据

## risk-engine-common 公共模块 工具相关

## risk-engine-service 业务模块 业务相关

## risk-engine-db dao持久化模块 数据库相关

## risk-engine-rest 引擎执行服务 风控引擎相关
## risk-engine-indicator 特征服务 计算特征值
## risk-engine-job job服务 消费消息和定时任务

## 一、序言

大家好，我是 X。随着自己做了三年信贷风控和两年币圈交易所风控，想总结一下这五年的风控经验，提高自己的市场竞争力，闲暇时刻也可以手撸一下这个项目，把自己的一些想法融入进去，还有一个对币圈 meme 牛市各种打狗各种 p 小将赚钱的消息满天飞，特别是 TRUMP 发 meme 币带来市场高潮，比特币四年减半带来的牛市，想分析链上数据包括筹码分布和活跃地址，聪明钱地址一段时间内的盈利能力，区块挖矿的难度系数，牛市的持续时间等等，他们之间存在有什么关系；监控聪明钱和聪明地址，监控交易转账数据，去跑风控系统，实时发送告警，监控市场行情，总结下来就是学习和搞钱，希望多结交有识之士，和大家一起愉快地玩耍。

交易所作为币圈的一个载体，承载者所有人淘金的梦想，所以我基于以上两个角度来作为风控系统方向，一个方向是链上数据，第二个是交易所业务数据，交易业务涵盖现货、合约、杠杆、理财、法币、KYC、活动以及链上充值提币等多种场景，各场景对风控的性能、实时性和准确性要求差异显著。例如，现货和合约交易需要毫秒级的实时风控，依赖纯内存操作以确保高性能；理财和法币交易对实时性要求稍低，但需同步返回结果；链上提币和充值则涉及异步处理和链上数据分析，需结合链上黑地址、智能合约行为及大数据分析进行风险评估。我将他们划分为链上风控、交易风控、业务风控；接下来的重点是：

- **第一链上风控**（前期主要搞这个）
- **第二交易风控**（暂时应该没时间）
- **第三业务风控**（可覆盖日常各行业）

设计一个全面的风控系统，旨在：

1. **保障资金安全**：防止用户资产因恶意行为或系统漏洞受损。
2. **合规性要求**：满足全球反洗钱（AML）、KYC（了解你的客户）及监管政策。
3. **提升用户体验**：在高性能风控下尽量减少对正常交易的干扰。
4. **应对多样化风险**：覆盖现货、合约、链上转账等多业务场景，结合链上数据分析，识别聪明钱、黑地址及异常交易模式。
5. **支持业务扩展**：模块化设计，适应未来新业务（如 DeFi、NFT 交易）的风控需求。
6. **监控链上数据**：实时发送告警，春江水暖鸭先知，赚钱先别人一步。

本系统将采用分层架构，结合实时内存计算、分布式消息队列、异步任务处理及链上大数据分析，满足不同业务场景的需求。

## 二、详细设计 - 远大愿景

### 1. 系统架构概述

风控系统采用分层架构，确保高性能、可扩展性和模块化设计：

- **输入层**：接收交易请求（现货、合约、杠杆等）、链上事件（充值、提币）及外部数据（链上区块、黑地址）。
- **实时处理层**：针对高实时性业务（现货、合约），使用纯内存风控引擎（如 Redis + Flink）。
- **同步处理层**：针对中等实时性业务（理财、法币），结合数据库和缓存。
- **异步处理层**：针对链上提币、充值及大数据分析，使用消息队列（如 RocketMQ）和定时任务。
- **规则引擎**：动态配置风控规则，支持多种维度（如交易量、频率、链上行为）。
- **存储层**：持久化交易记录、风控日志及链上数据（MySQL、Elasticsearch）。
- **分析层**：基于链上数据进行行为分析、聪明钱追踪及黑地址检测。

### 2. 业务场景及风控设计

#### 2.1 现货交易

- **需求**：实时性极高，毫秒级响应，纯内存操作。
- **风控角度**：
    - **交易频率**：检测单位时间内的高频下单/撤单（刷单、DDoS 攻击）。
    - **异常价格**：监控买卖单与市场深度偏差（如大单压盘、拉盘）。
    - **资金流向**：追踪账户间异常转账（洗钱）。
- **设计**：
    - 使用 Redis 存储用户交易计数器。
    - Flink 流处理实时计算市场深度和订单异常。
    - 规则引擎触发拦截（如频率超 100 次/秒）。
- **性能优化**：
    - 所有计算在内存完成，避免数据库 IO。
    - 异步记录交易日志到 Elasticsearch。

#### 2.2 合约交易

- **需求**：高实时性，支持杠杆倍数，需监控爆仓风险。
- **风控角度**：
    - **持仓风险**：监控杠杆倍数与账户余额比例。
    - **市场操纵**：检测异常爆仓（如恶意清算）、大额合约交易引发的穿仓。
    - **资金异常**：防止资金池被恶意套利。
    - **交易频率**：检测单位时间内的高频下单/撤单（刷单、DDoS 攻击）。
    - **合约清算**：价格波动超阈值时触发清算。

- **设计**：
    - Redis 存储用户持仓和余额快照。
    - Flink 实时计算风险率（持仓价值/保证金）。
    - 触发强制平仓或限制下单。
- **性能优化**：
    - 内存中维护市场价格缓存，减少外部查询。

#### 2.3 杠杆交易

- **需求**：实时性高，关注借贷风险。
- **风控角度**：
    - **借贷比例**：限制借贷金额与抵押品比例。
    - **还款逾期**：监控未及时还款账户。
    - **抵押品波动**：检测抵押资产价格异常。
- **设计**：
    - Redis 存储借贷记录和抵押品价值。
    - 定时任务 检查逾期还款。
    - 价格波动超阈值时触发清算。

#### 2.4 理财业务

- **需求**：同步返回，中等实时性。
- **风控角度**：
    - **资金来源**：检测充值资金是否异常（如黑池资金）。
    - **赎回频率**：限制异常频繁赎回。
    - **收益率异常**：监控理财产品收益波动。
- **设计**：
    - MySQL 存储理财记录，Redis 缓存用户操作。
    - 同步调用风控服务，返回结果。
    - 规则引擎检查资金来源（结合链上黑地址）。

#### 2.5 法币交易

- **需求**：同步返回，合规性强。
- **风控角度**：
    - **KYC/AML**：验证用户身份，检测洗钱行为。
    - **支付异常**：监控异常支付渠道或金额。
    - **地域限制**：限制高风险地区交易。
- **设计**：
    - 数据库存储 KYC 信息，Redis 缓存黑名单。
    - 同步调用第三方 AML 服务（如 Chainalysis、OKLinkAML）。
    - IP 地理位置检查（GeoIP 库）。

#### 2.6 链上提币

- **需求**：异步处理，安全性高。
- **风控角度**：
    - **提币地址**：检测目标地址是否为黑地址或聪明钱。
    - **提币金额**：限制大额提币，需人工审核。
    - **频率限制**：防止频繁小额提币（dusting attack）。
- **设计**：
    - RocketMQ 接收提币请求，异步处理。
    - 调用链上分析服务（见 2.8），检查地址风险。
    - 高额提币（> 2 BTC）触发多重签名审核。

#### 2.7 链上充值

- **需求**：异步确认，防双花。
- **风控角度**：
    - **充值确认**：等待足够区块确认（BTC 6 次，ETH 12 次）。
    - **来源地址**：检测充值地址是否为黑地址。
    - **异常金额**：监控异常大额充值。
- **设计**：
    - 链上监听服务（Web3j）捕获充值事件。
    - 异步任务验证确认数，更新账户余额。

#### 2.8 链上转账及黑地址/聪明钱风控

- **需求**：结合链上数据分析，实时/异步结合。
- **风控角度**：
    - **黑地址**：
        - **来源**：外部黑名单（如 OFAC、Chainalysis）。
        - **检测**：转账目标或来源是否命中黑地址。
    - **聪明钱**：
        - **定义**：高收益地址、早期参与者（如 DeFi 套利者）。
        - **检测**：追踪链上交易路径，分析利润率。
    - **异常模式**：
        - 高频小额转账（洗钱）。
        - 大额单笔转账（资金转移）。
- **设计**：
    - **数据采集**：blockstream.info/api (Bitcoin)、Web3j(Ethereum)、Solana SDK 抓取区块交易，存入 Elasticsearch。
    - **黑地址库**：Redis 存储黑地址集合（SET），实时匹配。
    - **聪明钱分析**：Flink 流处理计算地址收益，标记高风险地址。
    - **转账监控**：异步任务分析转账链路，生成风险评分。

#### 2.9 链上数据分析风控

- **需求**：基于大数据分析异常行为。
- **风控角度**：
    - **交易图谱**：构建地址间转账网络，检测团伙行为。
    - **区块分析**：监控 Gas 费异常、矿池集中度。
    - **智能合约**：检测异常调用（如闪电贷攻击）。
- **设计**：
    - **存储**：Elasticsearch 存储链上交易和区块数据。
    - **分析**：离线分析交易图谱，Flink 实时检测异常。
    - **规则**：动态更新聪明钱和黑地址库。

### 3. 系统组件设计

#### 3.1 实时风控引擎

- **技术**：Redis + Flink。
- **功能**：
    - 内存中计算交易频率、持仓风险等。
    - 实时触发拦截或报警。
- **性能**：单节点 QPS > 10万，延迟 < 1ms。

#### 3.2 规则引擎

- **技术**：自研规则引擎 Groovy 表达式。
- **功能**：
    - 支持动态规则配置（如“频率 > 100 次/秒”）。
    - 多维度组合（金额、频率、地址）。
- **管理**：Web 界面更新规则，实时生效。

#### 3.3 消息队列

- **技术**：RocketMQ、Kafka。
- **功能**：
    - 异步处理提币、充值及链上分析任务。
    - 保证消息不丢失（事务消息）。

#### 3.4 链上数据采集

- **技术**：Web3j (ETH)、Solana SDK (SOL)、BitcoinJ (BTC)。
- **功能**：
    - 实时监听区块和交易事件。
    - 数据清洗后存入 Elasticsearch。

#### 3.5 数据存储

- **MySQL**：交易记录、用户信息。
- **Redis**：实时缓存（黑名单、频率计数）。
- **Elasticsearch**：链上交易和日志。

#### 3.6 分析模块

- **Flink**：实时流处理（聪明钱、异常转账）。
- **Spark**：离线分析（交易图谱、历史趋势）。

#### 3.7 风控流程示例

##### 3.7.1 现货交易

1. 用户下单 -> Redis 计数频率。
2. Flink 检查市场深度 -> 异常触发拦截。
3. 订单通过 -> 异步记录日志。

##### 3.7.2 链上提币

1. 用户提交提币 -> RocketMQ 推送请求。
2. 异步任务调用链上分析 -> 黑地址命中则拒绝。
3. 高额提币 -> 多签审核 -> 执行提币。

##### 3.7.3 聪明钱检测

1. 链上数据采集 -> Elasticsearch 存储。
2. Flink 实时计算收益 -> 标记高风险地址。
3. 更新 Redis 黑名单 -> 影响交易和提币。

##### 3.7.4 性能与扩展性

- **实时业务**：Redis 单机 10万 QPS，Flink 分布式扩展。
- **同步业务**：数据库分库分表，缓存预热。
- **异步业务**：RocketMQ 水平扩展，任务动态分配。
- **链上分析**：Elasticsearch 集群存储 PB 级数据。

##### 3.7.5 安全与合规

- **加密**：敏感数据（如提币私钥）加密存储。
- **审计**：所有风控决策记录日志，存 Elasticsearch。
- **合规**：集成 AML/KYC 服务，定期更新黑名单。

##### 3.7.6 未来扩展

- **DeFi 风控**：监控智能合约交互。
- **NFT 交易**：检测异常铸造和转账。
- **跨链桥**：分析跨链资产流动风险。

## 三、项目

### 1. MySQL 表结构

#### 库名`risk`

```sql
-- auto-generated definition
CREATE TABLE blockchain_block (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    hash                VARCHAR(64) NOT NULL COMMENT '区块哈希',
    height              INT NOT NULL COMMENT '区块高度',
    version             INT NOT NULL COMMENT '版本',
    timestamp           BIGINT NOT NULL COMMENT '时间戳',
    tx_count            INT NOT NULL COMMENT '交易数量',
    size                INT NOT NULL COMMENT '区块大小',
    weight              INT NOT NULL COMMENT '区块重量',
    merkle_root         VARCHAR(64) NOT NULL COMMENT 'merkle root hash',
    previous_block_hash VARCHAR(64) NOT NULL COMMENT '父区块哈希',
    median_time         BIGINT NULL COMMENT '中位时间',
    nonce               BIGINT NOT NULL COMMENT '随机数',
    bits                INT NOT NULL COMMENT '难度目标',
    difficulty          BIGINT NOT NULL COMMENT '区块难度',
    coin                VARCHAR(32) NOT NULL COMMENT '币种',
    chain               VARCHAR(32) NOT NULL COMMENT '链',
    create_time         DATETIME NOT NULL COMMENT '创建时间'
) COMMENT '比特币区块信息表';

-- auto-generated definition
CREATE TABLE engine_result (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    incident_code        VARCHAR(255) NOT NULL COMMENT '关联事件编码',
    incident_name        VARCHAR(255) NOT NULL COMMENT '事件名称',
    request_payload      TEXT NULL COMMENT '请求载荷（JSON报文',
    hit_online_rules     TEXT NULL COMMENT '命中上线策略集合',
    hit_mock_rules       TEXT NULL COMMENT '命中模拟策略集合',
    rule_code            VARCHAR(255) NOT NULL COMMENT '匹配规则编码 分数最高',
    rule_name            VARCHAR(255) NOT NULL COMMENT '规则名称',
    rule_status          INT NOT NULL COMMENT '规则状态（0：删除，1：上线，2：下线，3：模拟）',
    rule_score           INT NOT NULL COMMENT '风险分数',
    rule_decision_result VARCHAR(255) NOT NULL COMMENT '决策结果',
    rule_label           VARCHAR(255) NULL COMMENT '标签',
    rule_penalty_action  VARCHAR(255) NULL COMMENT '处罚措施',
    rule_version         VARCHAR(255) NULL COMMENT '规则版本号',
    create_time          DATETIME NOT NULL COMMENT '创建时间'
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
create table indicator
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
        unique (indicator_code) comment '唯一索引，确保事件code不重复'
)
    comment '风控特征表';

create index idx_incident_code
    on indicator (incident_code)
    comment '索引，按事件code搜索';

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
    list_library_desc      text                                 null comment '名单描述',
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
    flow_no              varchar(64)                          not null comment '业务流水号',
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
    incident_code      varchar(255) not null comment '关联事件编码',
    rule_code          varchar(255) not null comment '规则编码',
    rule_name          varchar(255) not null comment '规则名称',
    status             int          not null comment '状态（0：删除，1：上线，2：下线，3：模拟）',
    groovy_script      text         null comment 'groovy可执行表达式',
    json_script        text         null comment '特征json数组',
    logic_script       varchar(255) null comment '逻辑表达式 1 && 2 || 3',
    score              int          not null comment '风险分数',
    decision_result    varchar(255) not null comment '决策结果 0拒绝 1成功',
    expiry_time        int          null comment '失效时间 单位h',
    label              varchar(255) null comment '标签文本',
    penalty_action     varchar(255) null comment '处罚措施',
    version            varchar(255) null comment '规则版本号',
    responsible_person varchar(255) not null comment '责任人',
    operator           varchar(255) not null comment '操作人',
    create_time        datetime     not null comment '创建时间',
    update_time        datetime     not null comment '修改时间',
    constraint rule_code
        unique (rule_code)
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


```sql

### 2. 系统设计

#### 2.1 设计详情

风控系统采用分层架构，确保高性能、可扩展性和模块化设计：

业务请求
- **加密**：--->引擎服务，校验唯一性业务唯一流水号和事件code，通过布隆过滤器校验是否命中黑名单，获取事件的策略，遍历策略获取策略的groovy表达式，通过json报文的特征表达式，
- **加密**：--->特征服务，特征服务根据特征类型获取特征值，特征值类型划分为用户特征（监控用户中心相关表kafka推送），三方服务特征（调用第三方API获取），计数器特征通过和flink
任务交互，发送kafka消息给flink任务，flink计算特征，再回写到redis

以下是转换成 Markdown 语法的内容，可以直接复制粘贴使用：

# 1.10 Transfer Record

## 数据库表结构

```sql
-- auto-generated definition
CREATE TABLE transfer_record (
    id              BIGINT AUTO_INCREMENT COMMENT '主键ID' PRIMARY KEY,
    send_address    VARCHAR(255)    NOT NULL COMMENT '发送地址',
    receive_address VARCHAR(255)    NOT NULL COMMENT '接收地址',
    amount          DECIMAL(30, 18) NOT NULL COMMENT '数量',
    u_amount        DECIMAL(30, 18) NULL COMMENT '折u价格',
    hash            VARCHAR(255)    NOT NULL COMMENT '交易哈希',
    height          INT             NOT NULL COMMENT '区块高度',
    chain           VARCHAR(255)    NOT NULL COMMENT '链',
    token           VARCHAR(255)    NOT NULL COMMENT '代币',
    fee             DECIMAL(30, 18) NULL COMMENT '手续费',
    transfer_time   BIGINT          NULL COMMENT '交易转账时间（毫秒级时间戳）',
    created_time    DATETIME        NOT NULL COMMENT '创建时间',
    status          INT             NOT NULL COMMENT '是否同步引擎执行（0或1表示状态）'
) COMMENT '交易转账表';

CREATE INDEX idx_chain_token ON transfer_record (chain, token);
CREATE INDEX idx_hash ON transfer_record (hash);
CREATE INDEX idx_receive_address ON transfer_record (receive_address);
CREATE INDEX idx_send_address ON transfer_record (send_address);



⸻

2. 系统设计

2.1 设计详情

风控系统采用 分层架构，确保高性能、可扩展性和模块化设计：
	1.	业务请求
	•	加密: 业务请求先进行加密处理
	•	引擎服务:
	•	校验唯一性（业务唯一流水号和事件 code）
	•	通过 布隆过滤器 校验是否命中黑名单
	•	获取 事件的策略，遍历策略，解析策略的 Groovy 表达式
	•	解析 JSON 报文，匹配特征表达式
	2.	特征服务
	•	获取特征值:
	•	用户特征: 监控用户中心相关表 Kafka 推送
	•	三方服务特征: 调用第三方 API 获取
	•	计数器特征:
	•	与 Flink 任务交互
	•	发送 Kafka 消息 给 Flink 任务
	•	Flink 计算特征，并回写到 Redis

这样格式化后，SQL 代码可以直接执行，系统设计部分也清晰易读，适合文档化管理。



