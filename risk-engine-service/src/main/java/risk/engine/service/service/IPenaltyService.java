package risk.engine.service.service;

import risk.engine.db.entity.PenaltyActionPO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 12:58
 * @Version: 1.0
 */
public interface IPenaltyService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(PenaltyActionPO record);

    PenaltyActionPO selectByPrimaryKey(Long id);

    List<PenaltyActionPO> selectByExample(PenaltyActionPO penalty);

    boolean updateByPrimaryKey(PenaltyActionPO record);

}
