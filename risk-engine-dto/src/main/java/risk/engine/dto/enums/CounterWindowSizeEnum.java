package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum CounterWindowSizeEnum {

    INTERVAL_5M("5m","5分钟"),
    INTERVAL_15M("15m","15分钟"),
    INTERVAL_30M("30m","30分钟"),
    INTERVAL_1H("1h","1小时"),
    INTERVAL_2H("2h","2小时"),
    INTERVAL_4H("4h","4小时"),
    INTERVAL_6H("6h","6小时"),
    INTERVAL_12H("12h","12小时"),
    INTERVAL_24H("24h","24小时"),
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
    CounterWindowSizeEnum(String value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static CounterWindowSizeEnum getRuleLabelEnumByCode(String code) {
        for (CounterWindowSizeEnum status : CounterWindowSizeEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
