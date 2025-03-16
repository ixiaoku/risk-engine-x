package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 指标来源
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum IndictorSourceEnum {

    // 事件定义
    ATTRIBUTE(1, "属性指标"),
    // 通过第三方API获取
    THIRD(2, "三方服务指标"),
    // 用户类指标
    USER(3, "用户类指标"),
    // 计数器指标 通过redis 计数或者flink
    COUNT(4, "计数器指标"),
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
    IndictorSourceEnum(Integer value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static IndictorSourceEnum getIncidentSourceEnumByCode(Integer code) {
        for (IndictorSourceEnum status : IndictorSourceEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
