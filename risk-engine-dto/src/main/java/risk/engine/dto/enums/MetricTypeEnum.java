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
public enum MetricTypeEnum {

    // 字符串类型
    STRING(1, "String"),
    // 长整型
    LONG(2, "Long"),
    // 整型
    INTEGER(3, "Integer"),
    // 布尔型
    BOOLEAN(4, "Boolean"),
    // 双精度浮点型
    DOUBLE(5, "Double"),
    // 单精度浮点型
    FLOAT(6, "Float"),
    // 字符型
    CHAR(7, "Char"),
    // 字节型
    BYTE(8, "Byte"),
    // 短整型
    SHORT(9, "Short"),
    // 大小数类型
    BIG_DECIMAL(10, "BigDecimal"),
    // json对象
    JSON_OBJECT(11, "JsonObject"),
    // jsonArray数组
    JSON_ARRAY(12, "JsonArray"),
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
    MetricTypeEnum(Integer value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static MetricTypeEnum getIncidentStatusEnumByCode(Integer code) {
        for (MetricTypeEnum status : MetricTypeEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
