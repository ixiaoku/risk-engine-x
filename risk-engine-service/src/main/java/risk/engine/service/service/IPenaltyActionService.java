package risk.engine.service.service;

import risk.engine.db.entity.PenaltyActionPO;
import risk.engine.dto.param.PenaltyActionParam;
import risk.engine.dto.vo.PenaltyFieldVO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 12:58
 * @Version: 1.0
 */
public interface IPenaltyActionService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(PenaltyActionPO record);

    PenaltyActionPO selectByPrimaryKey(Long id);

    List<PenaltyActionPO> selectByExample(PenaltyActionPO penaltyAction);

    List<PenaltyFieldVO> getPenaltyFields(PenaltyActionParam penalty);

    boolean updateByPrimaryKey(PenaltyActionPO record);

}
