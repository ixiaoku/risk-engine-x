package risk.engine.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/20 23:47
 * @Version: 1.0
 */
@Data
public class PageResult<T> {

    private int pageNum;

    private int pageSize;

    private Long total;

    private List<T> list;

}
