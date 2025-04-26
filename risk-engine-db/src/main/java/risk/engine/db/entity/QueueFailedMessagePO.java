package risk.engine.db.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Kafka 失败消息记录实体类
 */
@Data
public class QueueFailedMessagePO {
    /**
     * 主键
     */
    private Long id;

    /**
     * Kafka 主题
     */
    private String topic;

    /**
     * 分区
     */
    private Integer partition;

    /**
     * 偏移量
     */
    private Long offset;

    /**
     * 消息 Key
     */
    private String key;

    /**
     * 消息类型 kafka、rocketmq、rabbitmq
     */
    private String type;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 失败原因
     */
    private String errorMessage;

    /**
     * 消息状态：0失败 1成功
     */
    private Integer status;

    /**
     * 重试次数
     */
    private Integer retry;

    /**
     * 最大重试次数
     */
    private Integer maxRetry;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
