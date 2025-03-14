package risk.engine.db.dao;

import org.apache.ibatis.annotations.Mapper;
import risk.engine.db.entity.EngineResult;

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

    int insert(EngineResult record);

    List<EngineResult> selectByExample(EngineResult engineResult);

    EngineResult selectByPrimaryKey(Long id);

}