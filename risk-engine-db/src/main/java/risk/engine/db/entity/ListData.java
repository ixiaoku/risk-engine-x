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
public class ListData {

    private Long id;

    private String listLibraryCode;

    private String listLibraryName;

    private String listName;

    private String listCode;

    private String listValue;

    private Integer status;

    private Integer listType;

    private String operator;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String listDesc;

}