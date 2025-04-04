package risk.engine.db.dao;

import org.apache.ibatis.annotations.Mapper;
import risk.engine.db.entity.EngineResultPO;

import java.util.List;

/**
 * 引擎执行结果
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Mapper
public interface EngineResultMapper {

    int deleteByPrimaryKey(Long id);

    int insert(EngineResultPO record);

    List<EngineResultPO> selectByExample(EngineResultPO engineResult);

    EngineResultPO selectByPrimaryKey(Long id);

}