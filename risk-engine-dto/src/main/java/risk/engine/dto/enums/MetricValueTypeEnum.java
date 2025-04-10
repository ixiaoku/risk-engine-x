package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 指标值类型
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum MetricValueTypeEnum {

    //自定义
    CUSTOM("custom", "自定义"),
    //指标
    METRIC("metric", "指标"),
    ;

    /**
     * 枚举的整数值
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;

    // 枚举的构造函数，用于设置整数值
    MetricValueTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static MetricValueTypeEnum getMetricValueTypeEnumByCode(String code) {
        for (MetricValueTypeEnum status : MetricValueTypeEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
