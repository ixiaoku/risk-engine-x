package risk.engine.db.entity;

import lombok.Data;

import java.util.Date;

/**
 * 名单库
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Data
public class ListLibrary {

    private Long id;

    private String listLibraryCode;

    private String listLibraryName;

    private Boolean status;

    private Boolean listCategory;

    private String operator;

    private Date createTime;

    private Date updateTime;

    private String listLibrary;

}