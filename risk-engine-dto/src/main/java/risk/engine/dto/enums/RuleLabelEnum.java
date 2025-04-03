package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum RuleLabelEnum {

    BEHAVIOR(1,"行为模式"),
    TRADE(2,"交易行为"),
    ACCOUNT(3,"账户识别"),
    ASSET(4,"资金流向"),
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
    RuleLabelEnum(Integer value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static RuleLabelEnum getRuleLabelEnumByCode(Integer code) {
        for (RuleLabelEnum status : RuleLabelEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
