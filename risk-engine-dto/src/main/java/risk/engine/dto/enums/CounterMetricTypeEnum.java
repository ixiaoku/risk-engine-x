package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 指标字段数据类型
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum CounterMetricTypeEnum {

    // 长整型
    LONG(1, "Long"),
    // 整型
    INTEGER(2, "Integer"),
    // 大小数类型
    BIG_DECIMAL(3, "BigDecimal"),
    // 双精度浮点型
    DOUBLE(4, "Double"),
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
    CounterMetricTypeEnum(Integer value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static CounterMetricTypeEnum getIncidentStatusEnumByCode(Integer code) {
        for (CounterMetricTypeEnum status : CounterMetricTypeEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
