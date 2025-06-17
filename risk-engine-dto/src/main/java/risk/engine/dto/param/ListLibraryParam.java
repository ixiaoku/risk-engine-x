package risk.engine.dto.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: X
 * @Date: 2025/6/15 19:25
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
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
     * @see risk.engine.dto.enums.ListLibraryStatusEnum
     */
    private Integer status;
    /**
     * 名单库类别
     * @see risk.engine.dto.enums.ListLibraryCategoryEnum
     */
    private Integer listCategory;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 名单库描述
     */
    private String listLibraryDesc;

}
