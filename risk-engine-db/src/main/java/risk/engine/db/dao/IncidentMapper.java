package risk.engine.db.dao;

import org.apache.ibatis.annotations.Mapper;
import risk.engine.db.entity.IncidentPO;

import java.util.List;

/**
 * 事件
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Mapper
public interface IncidentMapper {

    int deleteByPrimaryKey(Long id);

    int insert(IncidentPO record);

    List<IncidentPO> selectByExample(IncidentPO incident);

    IncidentPO selectByIncidentCode(String incidentCode);

    IncidentPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(IncidentPO record);
}