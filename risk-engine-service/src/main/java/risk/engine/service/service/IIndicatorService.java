package risk.engine.service.service;

import risk.engine.db.entity.Indicator;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 12:55
 * @Version: 1.0
 */
public interface IIndicatorService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(Indicator record);

    Indicator selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(Indicator record);

    List<Indicator> selectByExample(Indicator record);

}
