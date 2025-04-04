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
public enum PenaltyActionEnum {


    APPEND_LIST("append_list", "加名单"),
    BUSINESS_WECHAT_BOT("business_wechat_bot", "企业微推送"),
    PERSON_WECHAT_BOT("person_wechat_bot", "个人微信推送"),
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
    PenaltyActionEnum(String value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param code 参数
     * @return 返回枚举
     */
    public static PenaltyActionEnum getPenaltyTypeEnumByCode(String code) {
        for (PenaltyActionEnum status : PenaltyActionEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
}
