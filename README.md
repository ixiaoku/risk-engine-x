# risk-engine-x 风控引擎系统X

risk-engine-crawler 爬虫模块 抓取链上信息

risk-engine-analysis 分析模块 分析清洗爬虫数据

risk-engine-common 公共模块 工具相关

risk-engine-service 业务模块 业务相关

risk-engine-db dao持久化模块 数据库相关

risk-engine-rest 引擎执行服务 风控引擎相关

以下是将你提供的内容转换为 Markdown 语法的版本，适合直接粘贴到 GitHub 的 README.md 文件中。我对内容进行了整理，添加了适当的标题、列表和代码块格式，确保清晰易读。
风控引擎系统X
一、序言
大家好，我是 X。在过去五年中，我从事了三年信贷风控和两年币圈交易所风控工作，想借此机会总结经验，提升市场竞争力。闲暇时，我计划手撸这个风控引擎项目，融入自己的想法。同时，币圈 meme 牛市、TRUMP 发 meme 币、比特币四年减半等事件引发市场热潮，我想通过分析链上数据（筹码分布、活跃地址、聪明钱盈利能力、挖矿难度、牛市持续时间等）探索它们之间的关系，监控聪明钱和黑地址，分析交易转账数据，运行风控系统，实时告警，追踪市场行情。总结来说，这既是学习，也是搞钱的过程，希望结交志同道合的朋友，一起愉快玩耍。
交易所作为币圈淘金的载体，承载着无数人的梦想。我从以下两个方向设计风控系统：
链上数据风控：分析链上行为，识别风险。
交易所业务数据风控：覆盖现货、合约等业务。
交易业务包括现货、合约、杠杆、理财、法币、KYC、活动以及链上充值提币等多种场景，各场景对风控的性能、实时性和准确性要求不同：
现货和合约：毫秒级实时风控，依赖纯内存操作，确保高性能。
理财和法币：中等实时性，需同步返回结果。
链上提币和充值：异步处理，结合链上黑地址、智能合约行为及大数据分析。
我将风控划分为：
第一：链上风控（前期重点）
第二：交易风控（暂无时间）
第三：业务风控（覆盖日常行业）
设计目标
保障资金安全：防止用户资产因恶意行为或漏洞受损。
合规性要求：满足全球 AML、KYC 及监管政策。
提升用户体验：在高性能风控下减少对正常交易的干扰。
应对多样化风险：覆盖现货、合约、链上转账等场景，结合链上数据分析，识别聪明钱、黑地址及异常模式。
支持业务扩展：模块化设计，适应未来新业务（如 DeFi、NFT 交易）。
监控链上数据：实时告警，先人一步。
系统采用分层架构，结合实时内存计算、分布式消息队列、异步任务处理及链上大数据分析，满足不同场景需求。
二、详细设计 - 远大前景
以下设计基于我的想法并由 AI 协助完善。
1. 系统架构概述
风控系统采用分层架构，确保高性能、可扩展性和模块化设计：
输入层：接收交易请求（现货、合约、杠杆等）、链上事件（充值、提币）及外部数据（链上区块、黑地址）。
实时处理层：针对高实时性业务（现货、合约），使用纯内存风控引擎（如 Redis + Flink）。
同步处理层：针对中等实时性业务（理财、法币），结合数据库和缓存。
异步处理层：针对链上提币、充值及大数据分析，使用消息队列（如 RocketMQ）和定时任务。
规则引擎：动态配置风控规则，支持多维度（如交易量、频率、链上行为）。
存储层：持久化交易记录、风控日志及链上数据（MySQL、Elasticsearch）。
分析层：基于链上数据进行行为分析、聪明钱追踪及黑地址检测。
2. 业务场景及风控设计
2.1 现货交易
需求：实时性极高，毫秒级响应，纯内存操作。
风控角度：
交易频率：检测单位时间内高频下单/撤单（刷单、DDoS 攻击）。
异常价格：监控买卖单与市场深度偏差（如大单压盘、拉盘）。
资金流向：追踪账户间异常转账（洗钱）。
设计：
使用 Redis 存储用户交易计数器（INCRBY 记录频率）。
Flink 流处理实时计算市场深度和订单异常。
规则引擎触发拦截（如频率超 100 次/秒）。
性能优化：
所有计算在内存完成，避免数据库 IO。
异步记录交易日志到 Elasticsearch。
2.2 合约交易
需求：高实时性，支持杠杆倍数，需监控爆仓风险。
风控角度：
持仓风险：监控杠杆倍数与账户余额比例。
市场操纵：检测异常爆仓（如恶意清算）。
资金异常：防止资金池被恶意套利。
设计：
Redis 存储用户持仓和余额快照。
Flink 实时计算风险率（持仓价值/保证金）。
触发强制平仓或限制下单。
性能优化：
内存中维护市场价格缓存，减少外部查询。
2.3 杠杆交易
需求：实时性高，关注借贷风险。
风控角度：
借贷比例：限制借贷金额与抵押品比例。
还款逾期：监控未及时还款账户。
抵押品波动：检测抵押资产价格异常。
设计：
Redis 存储借贷记录和抵押品价值。
定时任务（Quartz）检查逾期还款。
价格波动超阈值时触发清算。
2.4 理财业务
需求：同步返回，中等实时性。
风控角度：
资金来源：检测充值资金是否异常（如黑池资金）。
赎回频率：限制异常频繁赎回。
收益率异常：监控理财产品收益波动。
设计：
MySQL 存储理财记录，Redis 缓存用户操作。
同步调用风控服务，返回结果。
规则引擎检查资金来源（结合链上黑地址）。
2.5 法币交易
需求：同步返回，合规性强。
风控角度：
KYC/AML：验证用户身份，检测洗钱行为。
支付异常：监控异常支付渠道或金额。
地域限制：限制高风险地区交易。
设计：
数据库存储 KYC 信息，Redis 缓存黑名单。
同步调用第三方 AML 服务（如 Chainalysis）。
IP 地理位置检查（GeoIP 库）。
2.6 链上提币
需求：异步处理，安全性高。
风控角度：
提币地址：检测目标地址是否为黑地址或聪明钱。
提币金额：限制大额提币，需人工审核。
频率限制：防止频繁小额提币（dusting attack）。
设计：
RocketMQ 接收提币请求，异步处理。
调用链上分析服务（见 2.8），检查地址风险。
高额提币（> 10 BTC）触发多重签名审核。
2.7 链上充值
需求：异步确认，防双花。
风控角度：
充值确认：等待足够区块确认（BTC 6 次，ETH 12 次）。
来源地址：检测充值地址是否为黑地址。
异常金额：监控异常大额充值。
设计：
链上监听服务（Web3j）捕获充值事件。
异步任务验证确认数，更新账户余额。
2.8 链上转账及黑地址/聪明钱风控
需求：结合链上数据分析，实时/异步结合。
风控角度：
黑地址：
来源：外部黑名单（如 OFAC、Chainalysis）。
检测：转账目标或来源是否命中黑地址。
聪明钱：
定义：高收益地址、早期参与者（如 DeFi 套利者）。
检测：追踪链上交易路径，分析利润率。
异常模式：
高频小额转账（洗钱）。
大额单笔转账（资金转移）。
设计：
数据采集：Web3j/Solana SDK 抓取区块交易，存入 Elasticsearch。
黑地址库：Redis 存储黑地址集合（SET），实时匹配。
聪明钱分析：Flink 流处理计算地址收益，标记高风险地址。
转账监控：异步任务分析转账链路，生成风险评分。
2.9 链上数据分析风控
需求：基于大数据分析异常行为。
风控角度：
交易图谱：构建地址间转账网络，检测团伙行为。
区块分析：监控 Gas 费异常、矿池集中度。
智能合约：检测异常调用（如闪电贷攻击）。
设计：
存储：Elasticsearch 存储链上交易和区块数据。
分析：Spark 离线分析交易图谱，Flink 实时检测异常。
规则：动态更新聪明钱和黑地址库。
3. 系统组件设计
3.1 实时风控引擎
技术：Redis + Flink。
功能：
内存中计算交易频率、持仓风险等。
实时触发拦截或报警。
性能：单节点 QPS > 10万，延迟 < 1ms。
3.2 规则引擎
技术：自研规则引擎（Groovy 表达式）。
功能：
支持动态规则配置（如“频率 > 100 次/秒”）。
多维度组合（金额、频率、地址）。
管理：Web 界面更新规则，实时生效。
3.3 消息队列
技术：RocketMQ。
功能：
异步处理提币、充值及链上分析任务。
保证消息不丢失（事务消息）。
3.4 链上数据采集
技术：Web3j (ETH)、Solana SDK (SOL)、BitcoinJ (BTC)。
功能：
实时监听区块和交易事件。
数据清洗后存入 Elasticsearch。
3.5 数据存储
MySQL：交易记录、用户信息。
Redis：实时缓存（黑名单、频率计数）。
Elasticsearch：链上交易和日志。
3.6 分析模块
Flink：实时流处理（聪明钱、异常转账）。
Spark：离线分析（交易图谱、历史趋势）。
4. 风控流程示例
4.1 现货交易
用户下单 -> Redis 计数频率。
Flink 检查市场深度 -> 异常触发拦截。
订单通过 -> 异步记录日志。
4.2 链上提币
用户提交提币 -> RocketMQ 推送请求。
异步任务调用链上分析 -> 黑地址命中则拒绝。
高额提币 -> 多签审核 -> 执行提币。
4.3 聪明钱检测
链上数据采集 -> Elasticsearch 存储。
Flink 实时计算收益 -> 标记高风险地址。
更新 Redis 黑名单 -> 影响交易和提币。
5. 性能与扩展性
实时业务：Redis 单机 10万 QPS，Flink 分布式扩展。
同步业务：数据库分库分表，缓存预热。
异步业务：RocketMQ 水平扩展，任务动态分配。
链上分析：Elasticsearch 集群存储 PB 级数据。
6. 安全与合规
加密：敏感数据（如提币私钥）加密存储。
审计：所有风控决策记录日志，存 Elasticsearch。
合规：集成 AML/KYC 服务，定期更新黑名单。
7. 未来扩展
DeFi 风控：监控智能合约交互。
NFT 交易：检测异常铸造和转账。
跨链桥：分析跨链资产流动风险。
三、项目
1. MySQL 表结构
blockchain_block
比特币区块信息表，用于存储链上区块数据。
sql
CREATE TABLE blockchain_block (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    hash                VARCHAR(64) NOT NULL COMMENT '区块哈希',
    height              INT NOT NULL COMMENT '区块高度',
    version             INT NOT NULL COMMENT '版本',
    timestamp           BIGINT NOT NULL COMMENT '时间戳',
    tx_count            INT NOT NULL COMMENT '交易数量',
    size                INT NOT NULL COMMENT '区块大小',
    weight              INT NOT NULL COMMENT '区块重量',
    merkle_root         VARCHAR(64) NOT NULL COMMENT 'Merkle Root Hash',
    previous_block_hash VARCHAR(64) NOT NULL COMMENT '父区块哈希',
    median_time         BIGINT NULL COMMENT '中位时间',
    nonce               BIGINT NOT NULL COMMENT '随机数',
    bits                INT NOT NULL COMMENT '难度目标',
    difficulty          BIGINT NOT NULL COMMENT '区块难度',
    coin                VARCHAR(32) NOT NULL COMMENT '币种',
    chain               VARCHAR(32) NOT NULL COMMENT '链',
    create_time         DATETIME NOT NULL COMMENT '创建时间'
) COMMENT '比特币区块信息表';
engine_result
风控引擎结果表，存储风控规则命中记录。
sql
CREATE TABLE engine_result (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    incident_code        VARCHAR(255) NOT NULL COMMENT '关联事件编码',
    incident_name        VARCHAR(255) NOT NULL COMMENT '事件名称',
    request_payload      TEXT NULL COMMENT '请求载荷（JSON 报文）',
    hit_online_rules     TEXT NULL COMMENT '命中上线策略集合',
    hit_mock_rules       TEXT NULL COMMENT '命中模拟策略集合',
    rule_code            VARCHAR(255) NOT NULL COMMENT '匹配规则编码（分数最高）',
    rule_name            VARCHAR(255) NOT NULL COMMENT '规则名称',
    rule_status          INT NOT NULL COMMENT '规则状态（0：删除，1：上线，2：下线，3：模拟）',
    rule_score           INT NOT NULL COMMENT '风险分数',
    rule_decision_result VARCHAR(255) NOT NULL COMMENT '决策结果',
    rule_label           VARCHAR(255) NULL COMMENT '标签',
    rule_penalty_action  VARCHAR(255) NULL COMMENT '处罚措施',
    rule_version         VARCHAR(255) NULL COMMENT '规则版本号',
    create_time          DATETIME NOT NULL COMMENT '创建时间'
) COMMENT '风控引擎结果表';
