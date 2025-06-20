package risk.engine.db.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 名单数据
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Data
public class ListDataPO {

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
     * 名单名称
     */
    private String listName;
    /**
     * 名单编码
      */
    private String listCode;
    /**
     * 名单值
     */
    private String listValue;
    /**
     * 名单值类型
     */
    private Integer status;
    /**
     * 名单类型
     */
    private Integer listType;
    /**
     * 名单分类
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
     * 名单描述
     */
    private String listDesc;

}