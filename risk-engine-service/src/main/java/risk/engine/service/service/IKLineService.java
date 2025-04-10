package risk.engine.service.service;

import risk.engine.db.entity.KLinePO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/10 14:58
 * @Version: 1.0
 */
public interface IKLineService {

    boolean deleteByPrimaryKey(Long id);

    boolean batchInsert(List<KLinePO> list);

    List<KLinePO> selectByExample(KLinePO kLine);

    boolean updateByPrimaryKey(KLinePO kLine);

}
