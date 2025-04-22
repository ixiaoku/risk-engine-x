package risk.engine.db.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlarmRecordPO {

    private Long id;

    private String url;

    private String env;

    private String serveName;

    private String alarmType;

    private String alarmLevel;

    /**
     * 0失败 1成功 2待执行
     */
    private Integer status;

    private Integer retry;

    private String channel;

    private String message;

    private String stack;

    private String extraData;

    private LocalDateTime sendTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}