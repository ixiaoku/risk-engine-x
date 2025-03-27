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
     * 用户名
     */
    private final String username;

    /**
     * 用户id
     */
    private final String userId;

    // 枚举的构造函数，用于设置整数值
    TwitterUserEnum(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }

    /**
     * 根据整数值获取枚举实例的方法
     * @param username 参数
     * @return 返回枚举
     */
    public static TwitterUserEnum getTwitterUserEnumByCode(String username) {
        for (TwitterUserEnum user : TwitterUserEnum.values()) {
            if (Objects.equals(user.getUsername(), username)) {
                return user;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + username);
    }
}
