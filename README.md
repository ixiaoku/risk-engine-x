# risk-engine-x 风控引擎系统X

risk-engine-crawler 爬虫模块 抓取链上信息

risk-engine-analysis 分析模块 分析清洗爬虫数据

risk-engine-common 公共模块 工具相关

risk-engine-service 业务模块 业务相关

risk-engine-db dao持久化模块 数据库相关

risk-engine-rest 引擎执行服务 风控引擎相关






# 风控引擎系统X

## 一、序言

大家好，我是 X。五年风控经验（三年信贷，两年币圈交易所），借此总结经验，提升竞争力，闲暇时手撸此项目。币圈 meme 牛市、TRUMP 发币、比特币减半等事件引发热潮，我想分析链上数据（筹码分布、活跃地址、聪明钱盈利、挖矿难度、牛市周期等），探索其关系，监控聪明钱和黑地址，跑风控系统，实时告警，追踪行情，既学习又搞钱，希望结交朋友一起玩耍。

交易所承载淘金梦想，我从两方向设计风控：
1. **链上数据风控**：分析链上行为。
2. **交易所业务风控**：覆盖现货、合约等。

业务场景包括现货、合约、杠杆、理财、法币、KYC、活动及链上充值提币：
- **现货/合约**：毫秒级实时风控，纯内存操作。
- **理财/法币**：中等实时性，同步返回。
- **链上提币/充值**：异步处理，结合黑地址和大数据分析。

风控分类：
- **链上风控**（前期重点）
- **交易风控**（暂无时间）
- **业务风控**（覆盖日常行业）

### 设计目标
1. **资金安全**：防恶意行为和漏洞。
2. **合规性**：满足 AML/KYC 及监管要求。
3. **用户体验**：高性能下减少干扰。
4. **多样化风险**：覆盖多场景，识别聪明钱、黑地址。
5. **业务扩展**：模块化设计，支持 DeFi、NFT 等。
6. **链上监控**：实时告警，先人一步。

系统采用分层架构：内存计算、消息队列、异步任务、链上大数据分析。

---

## 二、详细设计 - 远大前景

### 1. 系统架构概述
分层架构，确保高性能、可扩展性：
- **输入层**：接收交易请求、链上事件、外部数据。
- **实时处理层**：现货/合约，使用 Redis + Flink。
- **同步处理层**：理财/法币，结合数据库和缓存。
- **异步处理层**：提币/充值，使用 RocketMQ + 定时任务。
- **规则引擎**：动态配置规则（交易量、频率等）。
- **存储层**：MySQL、Elasticsearch 持久化数据。
- **分析层**：链上行为分析、聪明钱追踪。

### 2. 业务场景及风控设计

#### 2.1 现货交易
- **需求**：毫秒级响应，纯内存。
- **风控**：
  - 交易频率：防刷单/DDoS。
  - 异常价格：监控压盘/拉盘。
  - 资金流向：防洗钱。
- **设计**：Redis 计数，Flink 流处理，规则引擎拦截。
- **优化**：内存计算，异步日志到 Elasticsearch。

#### 2.2 合约交易
- **需求**：高实时性，监控爆仓。
- **风控**：
  - 持仓风险：杠杆与余额比例。
  - 市场操纵：异常爆仓。
  - 资金异常：防套利。
- **设计**：Redis 快照，Flink 计算风险率，触发平仓。
- **优化**：内存缓存市场价格。

#### 2.3 杠杆交易
- **需求**：实时性高，关注借贷。
- **风控**：
  - 借贷比例：限制金额。
  - 还款逾期：监控账户。
  - 抵押品波动：检测异常。
- **设计**：Redis 存储，Quartz 检查逾期，波动触发清算。

#### 2.4 理财业务
- **需求**：同步返回，中等实时性。
- **风控**：
  - 资金来源：防黑池资金。
  - 赎回频率：限异常操作。
  - 收益率异常：监控波动。
- **设计**：MySQL 记录，Redis 缓存，规则检查。

#### 2.5 法币交易
- **需求**：同步返回，强合规。
- **风控**：
  - KYC/AML：身份验证，防洗钱。
  - 支付异常：监控渠道。
  - 地域限制：限高风险地区。
- **设计**：数据库存 KYC，Redis 黑名单，调用 AML 服务。

#### 2.6 链上提币
- **需求**：异步处理，安全性高。
- **风控**：
  - 提币地址：防黑地址/聪明钱。
  - 提币金额：限大额，需审核。
  - 频率限制：防 dusting attack。
- **设计**：RocketMQ 异步，链上分析，高额多签审核。

#### 2.7 链上充值
- **需求**：异步确认，防双花。
- **风控**：
  - 充值确认：BTC 6 次，ETH 12 次。
  - 来源地址：防黑地址。
  - 异常金额：监控大额。
- **设计**：Web3j 监听，异步验证。

#### 2.8 链上转账及黑地址/聪明钱风控
- **需求**：实时/异步分析。
- **风控**：
  - 黑地址：外部黑名单匹配。
  - 聪明钱：追踪盈利地址。
  - 异常模式：高频/大额转账。
- **设计**：Web3j 采集，Redis 黑名单，Flink 分析。

#### 2.9 链上数据分析风控
- **需求**：大数据分析。
- **风控**：
  - 交易图谱：检测团伙。
  - 区块分析：Gas 费异常。
  - 智能合约：防闪电贷。
- **设计**：Elasticsearch 存储，Spark/Flink 分析。

### 3. 系统组件设计
- **实时风控引擎**：Redis + Flink，QPS > 10万，延迟 < 1ms。
- **规则引擎**：Groovy 表达式，动态配置。
- **消息队列**：RocketMQ，异步处理。
- **链上采集**：Web3j/Solana SDK/BitcoinJ。
- **存储**：MySQL、Redis、Elasticsearch。
- **分析**：Flink 实时，Spark 离线。

### 4. 风控流程示例
#### 4.1 现货交易
1. 下单 -> Redis 计数。
2. Flink 检查 -> 拦截异常。
3. 通过 -> 异步日志。

#### 4.2 链上提币
1. 提交 -> RocketMQ 推送。
2. 链上分析 -> 黑地址拒绝。
3. 高额 -> 多签审核。

#### 4.3 聪明钱检测
1. 采集 -> Elasticsearch。
2. Flink 计算 -> 标记风险。
3. 更新 Redis 黑名单。

### 5. 性能与扩展性
- 实时：Redis 10万 QPS，Flink 分布式。
- 同步：数据库分库，缓存预热。
- 异步：RocketMQ 扩展。
- 链上：Elasticsearch PB 级存储。

### 6. 安全与合规
- 加密：敏感数据加密。
- 审计：日志存 Elasticsearch。
- 合规：集成 AML/KYC。

### 7. 未来扩展
- DeFi：监控合约交互。
- NFT：防异常铸造。
- 跨链：分析资产流动。

---

## 三、项目

### 1. MySQL 表结构

#### `blockchain_block`
比特币区块信息表。

```sql
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
