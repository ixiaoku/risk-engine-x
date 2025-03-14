package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 逻辑操作符
 * @Author: X
 * @Date: 2025/3/14 16:13
 * @Version: 1.0
 */
@Getter
public enum OperationSymbolEnum {

    // 大于
    GREATER_THAN(1, ">"),
    // 小于
    LESS_THAN(2, "<"),
    // 等于
    EQUAL_TO(3, "=="),
    // 不等于
    NOT_EQUAL_TO(4, "!="),
    // 大于等于
    GREATER_THAN_OR_EQUAL_TO(5, ">="),
    // 小于等于
    LESS_THAN_OR_EQUAL_TO(6, "<="),
    // 与（逻辑与）
    AND(7, "&&"),
    // 或（逻辑或）
    OR(8, "||"),
    // 非（逻辑非）
    NOT(9, "!"),
    // 异或（逻辑异或）
    XOR(10, "Logical XOR"),
    // 蕴含（如果...则...）
    IMPLIES(11, "Implies"),
    // 属于某个集合
    IN(12, "IN"),
    // 不属于某个集合
    NOT_IN(13, "NOT IN"),
    // 模糊匹配
    LIKE(14, "LIKE");

    /**
     * 枚举的整数值
     */
    private final Integer code;
    /**
     * 名字
     */
    private final String name;

    // 枚举的构造函数，用于设置整数值
    OperationSymbolEnum(Integer value, String name) {
        this.code = value;
        this.name = name;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static OperationSymbolEnum getOperationSymbolEnumByCode(Integer code) {
        for (OperationSymbolEnum status : OperationSymbolEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}

