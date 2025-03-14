package risk.engine.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import risk.engine.db.entity.EngineResult;

/**
 * 引擎执行结果
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Mapper
public interface EngineResultMapper {

    int deleteByPrimaryKey(Long id);

    int insert(EngineResult record);

    List<EngineResult> selectByExample(EngineResult engineResult);

    EngineResult selectByPrimaryKey(Long id);

    int updateByPrimaryKey(EngineResult record);
}