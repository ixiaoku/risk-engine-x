package risk.engine.dto.dto.engine;

import lombok.Data;

/**
 * @Author: X
 * @Date: 2025/4/6 17:19
 * @Version: 1.0
 */
@Data
public class EssentialElementDTO {

    /**
     * ip地址
     */
    private String ip;
    /**
     * 设备地址
     */
    private String device;
    /**
     * 用户id
     */
    private String uid;
    /**
     * 邮箱地址
     */
    private String email;
    /**
     * 电话号码
     */
    private String telephone;
    /**
     * 地理位置
     */
    private String location;

}
