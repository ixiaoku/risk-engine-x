package risk.engine.db.entity;

import lombok.Data;

import java.util.Date;

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

    private Boolean status;

    private Boolean listType;

    private String operator;

    private Date createTime;

    private Date updateTime;

    private String listDesc;

}