package risk.engine.dto.enums;

import lombok.Getter;

/**
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum RuleDecisionResultEnum {

    REJECT("0","拒绝"),
    SUCCESS("1","通过"),
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
    RuleDecisionResultEnum(String value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static RuleDecisionResultEnum getIncidentStatusEnumByCode(String code) {
        for (RuleDecisionResultEnum status : RuleDecisionResultEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
