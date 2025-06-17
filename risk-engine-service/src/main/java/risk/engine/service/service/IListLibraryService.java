package risk.engine.service.service;

import risk.engine.dto.PageResult;
import risk.engine.dto.param.ListLibraryParam;
import risk.engine.dto.vo.ListLibraryVO;

/**
 * @Author: X
 * @Date: 2025/3/16 12:57
 * @Version: 1.0
 */
public interface IListLibraryService {

    Boolean deleteByPrimaryKey(Long id);

    Boolean insert(ListLibraryParam param);

    ListLibraryVO selectByPrimaryKey(Long id);

    Boolean updateByPrimaryKey(ListLibraryParam param);

    PageResult<ListLibraryVO> list(ListLibraryParam param);

}
