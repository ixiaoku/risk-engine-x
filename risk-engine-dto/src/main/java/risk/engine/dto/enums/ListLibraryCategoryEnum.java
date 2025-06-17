package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum ListLibraryCategoryEnum {

    UID("uid", "用户ID"),
    ADDRESS("address", "地址"),
    DEVICE("device", "设备"),
    IP("ip", "IP地址"),
    EMAIL("email", "邮箱地址"),
    MOBILE("mobile", "手机号");
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
    ListLibraryCategoryEnum(String value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static ListLibraryCategoryEnum getListLibraryCategoryEnumByCode(String code) {
        for (ListLibraryCategoryEnum status : ListLibraryCategoryEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
