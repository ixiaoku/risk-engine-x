package risk.engine.db.entity.example;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 名单库
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Data
public class ListLibraryExample {

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


}