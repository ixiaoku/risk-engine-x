package risk.engine.db.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 名单库
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Data
public class ListLibraryPO {

    private Long id;

    private String listLibraryCode;

    private String listLibraryName;

    private Integer status;

    private Integer listCategory;

    private String operator;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String listLibraryDesc;

}