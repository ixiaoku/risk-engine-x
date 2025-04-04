package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum ExchangeEnum {

    BINANCE("BINANCE","币安公告监控"),
    OKEX("OKEX","OK公告监控"),
    BITGET("BITGET","BITGET公告监控"),
    COINBASE("COINBASE","COINBASE公告监控"),
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
    ExchangeEnum(String value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static ExchangeEnum getRuleLabelEnumByCode(String code) {
        for (ExchangeEnum status : ExchangeEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
