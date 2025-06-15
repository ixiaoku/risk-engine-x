package risk.engine.dto.param;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: X
 * @Date: 2025/6/15 19:25
 * @Version: 1.0
 */
@Data
public class ListLibraryParam extends PageHelperQuery {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 名单库编码
     */
    private String listLibraryCode;
    /**
     * 名单库名称
     */
    private String listLibraryName;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 名单库类别
     */
    private Integer listCategory;
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
    /**
     * 名单库描述
     */
    private String listLibraryDesc;

}
