package risk.engine.dto.enums;

import lombok.Getter;

/**
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum IncidentStatusEnum {
    // 删除状态，对应整数值0
    DELETED(0,"删除"),
    // 上线状态，对应整数值1
    ONLINE(1,"上线"),
    // 下线状态，对应整数值2
    OFFLINE(2,"下线");

    /**
     * 枚举的整数值
     */
    private final Integer code;
    /**
     * 描述
     */
    private final String desc;

    // 枚举的构造函数，用于设置整数值
    IncidentStatusEnum(Integer value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static IncidentStatusEnum getIncidentStatusEnumByCode(Integer code) {
        for (IncidentStatusEnum status : IncidentStatusEnum.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
