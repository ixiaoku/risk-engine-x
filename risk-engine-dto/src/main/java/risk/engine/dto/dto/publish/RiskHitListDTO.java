package risk.engine.dto.dto.publish;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: X
 * @Date: 2025/3/16 08:41
 * @Version: 1.0
 */
@Data
public class RiskHitListDTO {

    private Long id;

    /**
     * 名单库code
     */
    private String listLibraryCode;

    /**
     * 名单库名称
     */
    private String listLibraryName;

    /**
     * 名单数据名字
     */
    private String listName;

    /**
     * 名单数据code
     */
    private String listCode;

    /**
     * 名单数据
     */
    private String listValue;

    /**
     * 名单描述
     */
    private String listDesc;

    /**
     * 0未启用 1启用
     */
    private String status;

    /**
     * 名单类型 1地址 2ip 3设备id 4uid
     */
    private String listType;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
