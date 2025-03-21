package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * X用户
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum TwitterUserEnum {

    BWE("bwenews", "1483495485889564674"),
    CZ("heyibinance", "1003840309166366721"),
    HY("cz_binance", "902926941413453824"),
    ;

    /**
     * 整数值
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;

    // 枚举的构造函数，用于设置整数值
    TwitterUserEnum(String value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static TwitterUserEnum getTwitterUserEnumByCode(String code) {
        for (TwitterUserEnum status : TwitterUserEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
