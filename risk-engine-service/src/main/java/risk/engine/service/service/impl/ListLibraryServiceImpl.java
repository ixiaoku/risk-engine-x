package risk.engine.service.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.db.dao.ListLibraryMapper;
import risk.engine.db.entity.ListLibraryPO;
import risk.engine.db.entity.example.ListLibraryExample;
import risk.engine.dto.PageResult;
import risk.engine.dto.param.ListLibraryParam;
import risk.engine.dto.vo.ListLibraryVO;
import risk.engine.service.service.IListLibraryService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/3/16 12:57
 * @Version: 1.0
 */
@Service
public class ListLibraryServiceImpl implements IListLibraryService {

    @Resource
    private ListLibraryMapper listLibraryMapper;

    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return listLibraryMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(ListLibraryParam param) {
        ListLibraryPO listLibraryPO = new ListLibraryPO();
        listLibraryPO.setListLibraryCode(param.getListLibraryCode());
        listLibraryPO.setListLibraryName(param.getListLibraryName());
        listLibraryPO.setStatus(param.getStatus());
        listLibraryPO.setListCategory(param.getListCategory());
        listLibraryPO.setListLibraryDesc(param.getListLibraryDesc());
        listLibraryPO.setOperator(param.getOperator());
        listLibraryPO.setCreateTime(LocalDateTime.now());
        listLibraryPO.setUpdateTime(LocalDateTime.now());
        return listLibraryMapper.insert(listLibraryPO) > 0;
    }


    @Override
    public ListLibraryVO selectByPrimaryKey(Long id) {
        ListLibraryPO listLibraryPO = listLibraryMapper.selectByPrimaryKey(id);
        if (Objects.isNull(listLibraryPO)) {
            return null;
        }
        return convertVO(listLibraryPO);
    }

    @Override
    public boolean updateByPrimaryKey(ListLibraryParam param) {
        ListLibraryPO listLibraryPO = new ListLibraryPO();
        listLibraryPO.setId(param.getId());
        listLibraryPO.setListLibraryName(param.getListLibraryName());
        listLibraryPO.setStatus(param.getStatus());
        listLibraryPO.setListCategory(param.getListCategory());
        listLibraryPO.setListLibraryDesc(param.getListLibraryDesc());
        listLibraryPO.setOperator(param.getOperator());
        listLibraryPO.setUpdateTime(LocalDateTime.now());
        return listLibraryMapper.updateByPrimaryKey(listLibraryPO) > 0;
    }

    @Override
    public PageResult<ListLibraryVO> list(ListLibraryParam param) {
        PageResult<ListLibraryVO> pageResult = new PageResult<>();
        ListLibraryExample listLibraryQuery = new ListLibraryExample();
        listLibraryQuery.setListLibraryCode(param.getListLibraryCode());
        listLibraryQuery.setListLibraryName(param.getListLibraryName());
        listLibraryQuery.setStatus(param.getStatus());
        listLibraryQuery.setListCategory(param.getListCategory());
        listLibraryQuery.setStatus(param.getStatus());

        Page<ListLibraryPO> listLibraryPage = PageMethod.startPage(param.getPageNum(), param.getPageSize())
                .doSelectPage(() -> listLibraryMapper.selectByExample(listLibraryQuery));
        if (Objects.isNull(listLibraryPage) || CollectionUtils.isEmpty(listLibraryPage.getResult())) {
            return pageResult;
        }
        pageResult.setTotal(listLibraryPage.getTotal());
        pageResult.setPageNum(listLibraryPage.getPageNum());
        pageResult.setPageSize(listLibraryPage.getPageSize());
        pageResult.setList(listLibraryPage.getResult().stream()
                        .map(this::convertVO)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
        return pageResult;
    }

    private ListLibraryVO convertVO(ListLibraryPO listLibraryPO) {
        if (Objects.isNull(listLibraryPO)) {
            return null;
        }
        ListLibraryVO listLibraryVO = new ListLibraryVO();
        listLibraryVO.setId(listLibraryPO.getId());
        listLibraryVO.setListLibraryCode(listLibraryPO.getListLibraryCode());
        listLibraryVO.setListLibraryName(listLibraryPO.getListLibraryName());
        listLibraryVO.setStatus(listLibraryPO.getStatus());
        listLibraryVO.setListCategory(listLibraryPO.getListCategory());
        listLibraryVO.setOperator(listLibraryPO.getOperator());
        listLibraryVO.setCreateTime(DateTimeUtil.getTimeByLocalDateTime(listLibraryPO.getCreateTime()));
        listLibraryVO.setUpdateTime(DateTimeUtil.getTimeByLocalDateTime(listLibraryPO.getUpdateTime()));
        listLibraryVO.setListLibraryDesc(listLibraryPO.getListLibraryDesc());
        return listLibraryVO;

    }

}
