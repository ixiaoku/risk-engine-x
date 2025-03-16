package risk.engine.service.service;

import risk.engine.db.entity.Penalty;

/**
 * @Author: X
 * @Date: 2025/3/16 12:58
 * @Version: 1.0
 */
public interface IPenaltyService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(Penalty record);

    Penalty selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(Penalty record);

}
