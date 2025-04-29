package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @Author: X
 * @Date: 2025/4/29 10:56
 * @Version: 1.0
 */
@Getter
public enum CounterStatusEnum {

    ONLINE(0,"启用"),
    OFFLINE(1,"禁用"),
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
    CounterStatusEnum(Integer value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static CounterStatusEnum getCounterStatusEnumByCode(Integer code) {
        for (CounterStatusEnum status : CounterStatusEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }

}
