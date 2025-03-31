package risk.engine.dto.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 处罚类型
 * @Author: X
 * @Date: 2025/3/14 12:52
 * @Version: 1.0
 */
@Getter
public enum PenaltyTypeEnum {


    APPEND_LIST("append_list", "加名单"),
    BUSINESS_WECHAT_BOT("business_wechat_bot", "企业微信机器人"),
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
    PenaltyTypeEnum(String value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static PenaltyTypeEnum getListTypeEnumByCode(String code) {
        for (PenaltyTypeEnum status : PenaltyTypeEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
