package risk.engine.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: X
 * @Date: 2025/3/22 22:00
 * @Version: 1.0
 */
@Data
public class AsyncExecutorTaskDTO {

    private Long id;
    /**
     * 任务code
     */
    private String taskCode;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务描述
     */
    private String taskDes;
    /**
     * 任务状态 0失败 1成功 2待执行
     */
    private Integer status;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 任务结果
     */
    private String taskResult;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 重试次数
     */
    private Integer retryCount;
    /**
     * 最大次数
     */
    private Integer retryMax;
    /**
     * 任务参数
     */
    private String taskParams;
    /**
     * 业务实现-执行器
     */
    private String actuator;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
