package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 名单类型（1：地址，2：ip 3：设备 4：uid）
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum ListTypeEnum {

    // 地址
    ADDRESS(1, "地址"),
    // ip
    IP(2, "ip"),
    // 设备id
    DEVICE(3, "设备"),
    // uid
    UID(4, "uid"),
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
    ListTypeEnum(Integer value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static ListTypeEnum getListTypeEnumByCode(Integer code) {
        for (ListTypeEnum status : ListTypeEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
