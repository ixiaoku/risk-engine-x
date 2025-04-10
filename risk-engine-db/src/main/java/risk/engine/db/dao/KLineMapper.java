package risk.engine.db.dao;

import risk.engine.db.entity.KLinePO;

import java.util.List;

/**
 * k线
 * @Author: X
 * @Date: 2025/4/10 19:35
 * @Version: 1.0
 */
public interface KLineMapper {

    int deleteByPrimaryKey(Long id);

    int batchInsert(List<KLinePO> list);

    List<KLinePO> selectByExample(KLinePO kLine);

    int updateByPrimaryKey(KLinePO kLine);
}