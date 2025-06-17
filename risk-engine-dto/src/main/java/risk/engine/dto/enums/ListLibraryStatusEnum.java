package risk.engine.dto.enums;

import lombok.Getter;

/**
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum ListLibraryStatusEnum {

    ENABLED(1,"启用"),
    DISABLED(0,"禁用");

    /**
     * 枚举的整数值
     */
    private final Integer code;
    /**
     * 描述
     */
    private final String desc;

    // 枚举的构造函数，用于设置整数值
    ListLibraryStatusEnum(Integer value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static ListLibraryStatusEnum getIncidentStatusEnumByCode(Integer code) {
        for (ListLibraryStatusEnum status : ListLibraryStatusEnum.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
