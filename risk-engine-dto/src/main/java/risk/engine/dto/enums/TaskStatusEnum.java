package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 同步任务枚举
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum TaskStatusEnum {

    WAIT(0,"未同步"),
    SUCCESS(1,"已同步"),
    ;

    /**
     * 枚举的整数值
     */
    private final Integer code;
    /**
     * 描述
     */
    private final String desc;

    // 枚举的构造函数，用于设置整数值
    TaskStatusEnum(Integer value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static TaskStatusEnum getTaskStatusEnumByCode(Integer code) {
        for (TaskStatusEnum status : TaskStatusEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
